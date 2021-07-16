# 基本的な文法
- 参考にしたサイト
  - [wiki](https://ja.wikipedia.org/wiki/Java%E3%81%AE%E6%96%87%E6%B3%95)

## データ型
- 整数型
  - byte: 8bit符号付き
  - short: 16ビット符号付き
  - int: 32ビット符号付き
  - long: 64ビット符号付き
  - オーバーフローは例外等にはならず、wrap aroundされる
- 浮動小数点型
  - float: 32ビット符号付き
  - double: 64ビット符号付き
  - 浮動小数点は決して例外をスローしない
- 参照型
  - 全ての参照型はオブジェクト型を表すクラス”Object”から派生する
  - オブジェクトは参照型のインスタンスである
  - 列挙型Enumは抽象クラスEnumから暗黙的に派生する参照型である

## ビット演算子
- << 左シフト（ゼロ埋め）
- \>\> 右シフト（符号拡張！）
- \>\>\> 右シフト（ゼロ埋め）

## オブジェクト
- クラスあるいはインタフェースの内部で別のクラスを宣言できる
  - ネストされたクラス
  - `static`修飾されていない場合「内部クラス」となる
    - 内部クラスは外側のクラスのインスタンスを暗黙的にキャプチャすることで、静的メンバ、非静的メンバいずれにもアクセスすることができる。
- クラス宣言時に、以下の修飾子をつけることができる
  - abstract: インスタンスかできない
    - インタフェースとabstractクラスだけが抽象メソッドを持つことができる
    - 抽象クラスを継承する具象サブクラスは、引き継がれた全ての抽象メソッドを、abstractでないメソッドでオーバーライドしなければならない
  - final: サブクラスを作らせない
    - finalクラスの全てのメソッドは無条件にfinalになる
  - strictfp: このクラスと全てのネストクラスにおいて、全ての浮動小数点演算は厳密な浮動小数点動作を使用する

### インタフェース
- 実装の詳細が一切ない抽象型
- 抽象化ークラスの実装方法を隠蔽することーという考え方を強制してくれる
- 抽象メソッドと定数フィールド（static finalフィールド）だけを含むことができる
  - インタフェースメソッドはデフォルトでpublicかつabstract（実装を持たない）
  - インタフェースフィールドはデフォルトでpublic static finalである
  - インタフェースフィールドやクラスフィールドすなわち状態を持つことができない点が抽象クラスと異なる
- クラスは、1つのクラスを継承（extends）できるのに加えて、implementsキーワードを用いて1つ以上のインタフェースを実装できる

### アクセス修飾子
- そのクラスやクラスメンバにアクセス可能なクラスを決定する
- トップレベルクラスアクセス
  - それら自身のJavaパッケージからのみアクセスできる。これは、クラスのパッケージが、裏に隠れて機能を実行するようなAPIを提供することを可能とする。
    - public: 定義されたパッケージの外のクラスからもアクセス可能
- クラスメンバアクセス（上から厳しい順）
  1. private: そのクラスからのみアクセス可能
  2. package-private(修飾子を省略した場合)
  3. protected: 上に加え、パッケージ外の継承クラスからアクセス可能
  4. public: 任意のクラスからアクセス可能 

### フィールド
- アクセス修飾子に加えて、データフィールドは以下の修飾子によって宣言される
  - final: フィールドの中身は変更できない。
  - static: クラスのインスタンスではなくクラスに属する
    - ”クラスに”共通の変数
  - transient: オブジェクトの中でも永続的にできないフィールド
  - volatile: そのフィールドが他スレッドによって非同期にアクセスされる可能性があることをコンパイラに知らせる。finalにはなれない
- staticとfinal両方を宣言されたフィールドは事実上、定数である

### メソッド
- アクセス修飾子に加えて、メソッドには以下の修飾子をつけて宣言できる
  - abstract: 該当クラスでは定義されないメソッドであり、代わりに当該クラスの全ての具象サブクラスによって定義されなければならない
  - final: サブクラスによって再定義できないメソッド
  - static: クラスのインスタンスではなく、クラスに属する
- privateメソッドは自ずからfinalであり、abstractにはできない


## そのほか

### Map
```Java
private Map <String, Integer> map;
if (!map.containsKey(key)) {
    map.put(key, 0);
}
map.get(key);
map.put(key, map.get(key) + 1);
```


## UIスレッド
- JavaアプリケーションにおけるメインスレッドをAndroidでは「UIスレッド」と呼んでいる
- Androidはキーのイベント配信等を行うスレッドが1つの「シングルスレッド」モデル
- 思い処理をメインスレッド（UIスレッド）で行うと、画面の処理が止まったように見える
- 思い処理はマルチスレッドw使い、バックグラウンド（ワーカースレッド）で処理する必要がある