# 設計文書

## 概要

「明日から本気出す」にインスパイアされたTODOアプリをKotlinとJetpack Composeを使用して開発します。MVVMアーキテクチャパターンを採用し、Room データベースでローカルデータを管理し、直感的なタブベースのUIを提供します。

## アーキテクチャ

### アーキテクチャパターン
- **MVVM (Model-View-ViewModel)**: UI ロジックとビジネスロジックを分離
- **Repository パターン**: データアクセスを抽象化
- **依存性注入**: Hiltを使用してコンポーネント間の依存関係を管理

### レイヤー構成
```
Presentation Layer (UI)
├── Composables (Jetpack Compose UI)
├── ViewModels
└── Navigation

Domain Layer
├── Use Cases
├── Repository Interfaces
└── Models

Data Layer
├── Repository Implementations
├── Room Database
├── DAOs
└── Entities
```

## コンポーネントとインターフェース

### データモデル

#### Task Entity
```kotlin
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val priority: Priority,
    val category: TaskCategory,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class Priority(val displayName: String, val color: Color) {
    HIGH("高", Color.Red),
    MEDIUM("中", Color.Yellow),
    LOW("低", Color.Green)
}

enum class TaskCategory(val displayName: String) {
    TODAY("今日"),
    TOMORROW("明日"),
    SOMEDAY("いつか")
}
```

### データベース層

#### TaskDao
```kotlin
@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE category = :category AND isCompleted = 0 ORDER BY priority ASC, createdAt DESC")
    fun getTasksByCategory(category: TaskCategory): Flow<List<Task>>
    
    @Insert
    suspend fun insertTask(task: Task)
    
    @Update
    suspend fun updateTask(task: Task)
    
    @Delete
    suspend fun deleteTask(task: Task)
    
    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: String): Task?
}
```

#### AppDatabase
```kotlin
@Database(
    entities = [Task::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
```

### Repository層

#### TaskRepository Interface
```kotlin
interface TaskRepository {
    fun getTasksByCategory(category: TaskCategory): Flow<List<Task>>
    suspend fun insertTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun getTaskById(id: String): Task?
    suspend fun moveTaskToCategory(taskId: String, newCategory: TaskCategory)
}
```

### ViewModel層

#### MainViewModel
```kotlin
@HiltViewModel
class MainViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    
    private val _selectedTab = MutableStateFlow(TaskCategory.TODAY)
    val selectedTab: StateFlow<TaskCategory> = _selectedTab.asStateFlow()
    
    val todayTasks = taskRepository.getTasksByCategory(TaskCategory.TODAY)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    
    val tomorrowTasks = taskRepository.getTasksByCategory(TaskCategory.TOMORROW)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    
    val somedayTasks = taskRepository.getTasksByCategory(TaskCategory.SOMEDAY)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    
    fun selectTab(category: TaskCategory) {
        _selectedTab.value = category
    }
    
    fun completeTask(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(task.copy(isCompleted = true))
        }
    }
    
    fun moveTask(taskId: String, newCategory: TaskCategory) {
        viewModelScope.launch {
            taskRepository.moveTaskToCategory(taskId, newCategory)
        }
    }
}
```

### UI層

#### メイン画面構成
```kotlin
@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    
    Column {
        TabRow(selectedTabIndex = selectedTab.ordinal) {
            TaskCategory.values().forEach { category ->
                Tab(
                    selected = selectedTab == category,
                    onClick = { viewModel.selectTab(category) },
                    text = { Text(category.displayName) }
                )
            }
        }
        
        when (selectedTab) {
            TaskCategory.TODAY -> TaskList(
                tasks = viewModel.todayTasks.collectAsState().value,
                onTaskComplete = viewModel::completeTask,
                onTaskMove = viewModel::moveTask
            )
            TaskCategory.TOMORROW -> TaskList(
                tasks = viewModel.tomorrowTasks.collectAsState().value,
                onTaskComplete = viewModel::completeTask,
                onTaskMove = viewModel::moveTask
            )
            TaskCategory.SOMEDAY -> TaskList(
                tasks = viewModel.somedayTasks.collectAsState().value,
                onTaskComplete = viewModel::completeTask,
                onTaskMove = viewModel::moveTask
            )
        }
    }
}
```

## データモデル

### Task データモデル
- **id**: 一意識別子（UUID）
- **title**: タスクのタイトル（必須）
- **description**: タスクの詳細説明（オプション）
- **priority**: 優先度（HIGH, MEDIUM, LOW）
- **category**: カテゴリ（TODAY, TOMORROW, SOMEDAY）
- **isCompleted**: 完了状態
- **createdAt**: 作成日時
- **updatedAt**: 更新日時

### 優先度システム
- **高優先度**: 赤色インジケーター、最優先表示
- **中優先度**: 黄色インジケーター、中間表示
- **低優先度**: 緑色インジケーター、最後に表示

## エラーハンドリング

### データベースエラー
- Room データベース操作時の例外をキャッチ
- ユーザーフレンドリーなエラーメッセージを表示
- ログ出力によるデバッグ情報の記録

### バリデーションエラー
- タスクタイトルの空文字チェック
- 不正なデータ入力の検証
- UI レベルでの入力制限

### 例外処理パターン
```kotlin
sealed class Result<T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error<T>(val exception: Throwable) : Result<T>()
}

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {
    
    override suspend fun insertTask(task: Task) {
        try {
            taskDao.insertTask(task)
        } catch (e: Exception) {
            Log.e("TaskRepository", "Failed to insert task", e)
            throw TaskInsertException("タスクの保存に失敗しました", e)
        }
    }
}
```

## テスト戦略

### 単体テスト
- **Repository テスト**: データアクセス層のテスト
- **ViewModel テスト**: ビジネスロジックのテスト
- **Use Case テスト**: ドメインロジックのテスト

### 統合テスト
- **Database テスト**: Room データベースの動作確認
- **Repository 統合テスト**: DAOとRepositoryの連携テスト

### UI テスト
- **Compose テスト**: UI コンポーネントの動作確認
- **ナビゲーションテスト**: 画面遷移の確認
- **ユーザーインタラクションテスト**: タップ、スワイプ操作の確認

### テストツール
- **JUnit 5**: 単体テストフレームワーク
- **Mockk**: Kotlinモックライブラリ
- **Room Testing**: インメモリデータベーステスト
- **Compose Testing**: UI テストライブラリ
- **Espresso**: Android UI テスト（必要に応じて）

### テストカバレッジ目標
- Repository層: 90%以上
- ViewModel層: 85%以上
- Use Case層: 90%以上
- UI層: 70%以上（主要なユーザーフロー）