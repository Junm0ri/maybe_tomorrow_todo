# 明日から本気出す - TODOアプリ

「[明日から本気出す (by Pistatium様)](https://www.amazon.co.jp/Pistatium-%E6%98%8E%E6%97%A5%E3%81%8B%E3%82%89%E6%9C%AC%E6%B0%97%E5%87%BA%E3%81%99/dp/B017UHX0LQ)」にインスパイアされた、タスクを先延ばしにしながら管理できるAndroid TODOアプリです。
愛用していたアプリが非公開になっているので、「Claude Codeでサクッと同じような体験のアプリを作れないか？」の検証を含めてやってみた結果がこのリポジトリです。

## 概要

このアプリは、タスクを「今日」「明日」「いつか」の3つのカテゴリに分けて管理できるシンプルなTODOアプリです。先延ばしの心理を受け入れながら、効果的にタスク管理を行うことができます。

## 主な機能

- **3つのカテゴリでタスク管理**
  - 今日: 今日やるべきタスク
  - 明日: 明日に回すタスク
  - いつか: いつかやるタスク

- **タスク管理機能**
  - タスクの追加・編集・削除
  - 優先度設定（高・中・低）
  - タスクの完了マーク
  - カテゴリ間でのタスク移動
  - タスクの詳細説明の追加

- **視覚的な優先度表示**
  - 高優先度: 赤色
  - 中優先度: 黄色
  - 低優先度: 緑色

## 技術スタック

- **言語**: Kotlin
- **UI**: Jetpack Compose
- **アーキテクチャ**: MVVM (Model-View-ViewModel)
- **依存性注入**: Hilt
- **データベース**: Room
- **ナビゲーション**: Navigation Compose
- **非同期処理**: Kotlin Coroutines & Flow

## プロジェクト構造

```
app/src/main/java/com/example/myapplication/
├── data/                     # データ層
│   ├── local/               # ローカルデータソース
│   │   ├── dao/            # Data Access Objects
│   │   ├── database/       # Room Database
│   │   ├── entity/         # データベースエンティティ
│   │   └── converter/      # Type Converters
│   ├── mapper/             # エンティティ⇔ドメインモデル変換
│   └── repository/         # Repository実装
├── domain/                  # ドメイン層
│   ├── model/              # ドメインモデル
│   └── repository/         # Repository インターフェース
├── presentation/            # プレゼンテーション層
│   ├── navigation/         # ナビゲーション設定
│   ├── ui/                 # UIコンポーネント
│   │   ├── components/     # 再利用可能なコンポーネント
│   │   ├── screens/        # 画面
│   │   └── theme/          # テーマ設定
│   └── viewmodel/          # ViewModels
├── di/                      # Hiltモジュール
├── MainActivity.kt          # メインアクティビティ
└── TodoApplication.kt       # アプリケーションクラス
```

## セットアップ

### 必要環境

- Android Studio Hedgehog | 2023.1.1 以降
- JDK 11以上
- Android SDK (最小API 24, ターゲットAPI 36)

### ビルド手順

1. プロジェクトをクローンまたはダウンロード
2. Android Studioでプロジェクトを開く
3. Gradle Syncを実行
4. デバイスまたはエミュレータでアプリを実行

## 使い方

1. **タスクの追加**
   - 右下の「＋」ボタンをタップ
   - タイトルと説明（任意）を入力
   - 優先度とカテゴリを選択
   - 「保存」をタップ

2. **タスクの編集**
   - タスクをタップして編集画面を開く
   - 内容を変更して「更新」をタップ

3. **タスクの完了**
   - タスク左側のチェックボタンをタップ

4. **タスクの移動**
   - タスクを長押しまたは右側のメニューから「移動」を選択
   - 移動先のカテゴリを選択

5. **タスクの削除**
   - タスク右側のメニューから「削除」を選択

## アーキテクチャ

このアプリはクリーンアーキテクチャの原則に基づいて設計されています：

- **Presentation Layer**: UI表示とユーザーインタラクション
- **Domain Layer**: ビジネスロジックとモデル定義
- **Data Layer**: データの永続化とデータソース管理

データフローは単方向で、ViewModelがRepositoryを通じてデータを取得し、StateFlowを使用してUIに反映されます。

## ライセンス

このプロジェクトは学習目的で作成されました。

## 作成者

Kiroを使用して仕様から自動生成されました。
実装はClaude Codeによって行われました。