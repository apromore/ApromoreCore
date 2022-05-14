/*
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
if(!Apromore) var Apromore = {};

if(!Apromore.I18N) Apromore.I18N = {};

Apromore.I18N.Language = "ja_jp"; //Pattern <ISO language code>_<ISO country code> in lower case!

if(!Apromore.I18N.Apromore) Apromore.I18N.Apromore = {};

Apromore.I18N.Apromore.title		= "Apromore";
Apromore.I18N.Apromore.noBackendDefined	= "注意！ \nバックエンドが定義されていません。\n要求されたモデルを読み込むことができません。 Saveプラグインを使って設定を読み込んでみてください。";
Apromore.I18N.Apromore.pleaseWait 	= "読み込み中です。お待ちください...";
Apromore.I18N.Apromore.notLoggedOn = "ログオンしてません";
Apromore.I18N.Apromore.editorOpenTimeout = "エディタがまだ起動していないようです。ポップアップブロック機能が有効になっていないか確認し、無効にするか、このサイトのポップアップを許可してください。当サイトでは、広告を表示することはありません。";

if(!Apromore.I18N.AddDocker) Apromore.I18N.AddDocker = {};

Apromore.I18N.AddDocker.group = "Docker";
Apromore.I18N.AddDocker.add = " Dockerの追加";
Apromore.I18N.AddDocker.addDesc = " Dockerをクリックしてエッジに追加する";
Apromore.I18N.AddDocker.del = " Dockerの削除";
Apromore.I18N.AddDocker.delDesc = " Dockerをひとつ削除";

if(!Apromore.I18N.Arrangement) Apromore.I18N.Arrangement = {};

Apromore.I18N.Arrangement.groupZ = " Zオーダー";
Apromore.I18N.Arrangement.btf = "最前面へ移動";
Apromore.I18N.Arrangement.btfDesc = "最前面へ移動";
Apromore.I18N.Arrangement.btb = "最背面へ移動";
Apromore.I18N.Arrangement.btbDesc = "最背面へ移動";
Apromore.I18N.Arrangement.bf = "前面へ移動";
Apromore.I18N.Arrangement.bfDesc = "前面へ移動";
Apromore.I18N.Arrangement.bb = "背面へ移動";
Apromore.I18N.Arrangement.bbDesc = "背面へ移動";
Apromore.I18N.Arrangement.groupA = "揃える";
Apromore.I18N.Arrangement.ab = "下端に揃える";
Apromore.I18N.Arrangement.abDesc = "下端";
Apromore.I18N.Arrangement.am = "中央に揃える";
Apromore.I18N.Arrangement.amDesc = "中央";
Apromore.I18N.Arrangement.at = "上端に揃える";
Apromore.I18N.Arrangement.atDesc = "上端";
Apromore.I18N.Arrangement.al = "左端に揃える";
Apromore.I18N.Arrangement.alDesc = "左端";
Apromore.I18N.Arrangement.ac = "中心に揃える";
Apromore.I18N.Arrangement.acDesc = "中心";
Apromore.I18N.Arrangement.ar = "右端に揃える";
Apromore.I18N.Arrangement.arDesc = "右端";
Apromore.I18N.Arrangement.as = "同じ大きさに揃える";
Apromore.I18N.Arrangement.asDesc = "同じ大きさ";

if(!Apromore.I18N.Edit) Apromore.I18N.Edit = {};

Apromore.I18N.Edit.group = "編集";
Apromore.I18N.Edit.cut = "カット";
Apromore.I18N.Edit.cutDesc = "選択範囲をApromoreのクリップボードにカットする";
Apromore.I18N.Edit.copy = "コピー";
Apromore.I18N.Edit.copyDesc = "選択範囲をApromoreのクリップボードにコピーする";
Apromore.I18N.Edit.paste = "ペースト";
Apromore.I18N.Edit.pasteDesc = " Apromoreのクリップボードをキャンバスにペーストする";
Apromore.I18N.Edit.del = "削除";
Apromore.I18N.Edit.delDesc = "選択した図形をすべて削除する";

if(!Apromore.I18N.Save) Apromore.I18N.Save = {};

Apromore.I18N.Save.group = "ファイル";
Apromore.I18N.Save.save = "保存";
Apromore.I18N.Save.saveDesc = "保存";
Apromore.I18N.Save.saveAs = "名前を付けて保存";
Apromore.I18N.Save.saveAsDesc = "名前を付けて保存";
Apromore.I18N.Save.unsavedData = "保存されていないデータがありますので、画面を閉じる前に保存してください。";
Apromore.I18N.Save.newProcess = "新しいプロセス";
Apromore.I18N.Save.saveAsTitle = "名前を付けて保存";
Apromore.I18N.Save.saveBtn = "保存";
Apromore.I18N.Save.close = "閉じる";
Apromore.I18N.Save.savedAs = "として保存しました";
Apromore.I18N.Save.saved = "保存しました！";
Apromore.I18N.Save.failed = "保存に失敗しました。";
Apromore.I18N.Save.noRights = "変更を保存する権限はありません。";
Apromore.I18N.Save.saving = "保存中";
Apromore.I18N.Save.saveAsHint = "プロセス図は以下のように保存されています。";

if(!Apromore.I18N.Bimp) Apromore.I18N.Bimp = {};

Apromore.I18N.Bimp.group = "BIMP";
Apromore.I18N.Bimp.upload = "アップロード";
Apromore.I18N.Bimp.uploadDesc = " BIMPシミュレータにファイルをアップロード";

if(!Apromore.I18N.File) Apromore.I18N.File = {};

Apromore.I18N.File.group = "ファイル";
Apromore.I18N.File.print = "印刷";
Apromore.I18N.File.printDesc = "現在のモデルを印刷する";
Apromore.I18N.File.pdf = " PDFとしてエクスポート";
Apromore.I18N.File.pdfDesc = " PDFとしてエクスポート";
Apromore.I18N.File.info = "情報";
Apromore.I18N.File.infoDesc = "情報";
Apromore.I18N.File.genPDF = " PDFの生成";
Apromore.I18N.File.genPDFFailed = " PDFの生成に失敗しました。";
Apromore.I18N.File.printTitle = "印刷";
Apromore.I18N.File.printMsg = "現在、印刷機能に不具合が発生しています。ダイアグラムの印刷には、「PDFエクスポート」の使用をお勧めします。本当に印刷を続けますか？";

Apromore.I18N.File.svg = ".SVGとしてエクスポート";
Apromore.I18N.File.svgDesc = ".SVGとしてエクスポート";
Apromore.I18N.File.bpmn = ".BPMNとしてエクスポート";
Apromore.I18N.File.bpmnDesc = ".BPMNとしてエクスポート";

if(!Apromore.I18N.Grouping) Apromore.I18N.Grouping = {};

Apromore.I18N.Grouping.grouping = "グループ化";
Apromore.I18N.Grouping.group = "グループ";
Apromore.I18N.Grouping.groupDesc = "選択されたすべての図形をグループ化";
Apromore.I18N.Grouping.ungroup = "グループ化解除";
Apromore.I18N.Grouping.ungroupDesc = "選択したすべての図形のグループを削除する";

if(!Apromore.I18N.Loading) Apromore.I18N.Loading = {};

Apromore.I18N.Loading.waiting ="お待ちください...";

if(!Apromore.I18N.PropertyWindow) Apromore.I18N.PropertyWindow = {};

Apromore.I18N.PropertyWindow.name = "名前";
Apromore.I18N.PropertyWindow.value = "値";
Apromore.I18N.PropertyWindow.selected = "選択済";
Apromore.I18N.PropertyWindow.clickIcon = "アイコンをクリック";
Apromore.I18N.PropertyWindow.add = "追加";
Apromore.I18N.PropertyWindow.rem = "削除";
Apromore.I18N.PropertyWindow.complex = "複雑なタイプのためのエディタ";
Apromore.I18N.PropertyWindow.text = "テキストタイプのエディタ";
Apromore.I18N.PropertyWindow.ok = "Ok";
Apromore.I18N.PropertyWindow.cancel = "キャンセル";
Apromore.I18N.PropertyWindow.dateFormat = " y/m/d ";

if(!Apromore.I18N.ShapeMenuPlugin) Apromore.I18N.ShapeMenuPlugin = {};

Apromore.I18N.ShapeMenuPlugin.drag = "ドラッグ";
Apromore.I18N.ShapeMenuPlugin.clickDrag = "クリックまたはドラッグ";
Apromore.I18N.ShapeMenuPlugin.morphMsg = "図形の変形";

if(!Apromore.I18N.SyntaxChecker) Apromore.I18N.SyntaxChecker = {};

Apromore.I18N.SyntaxChecker.group = "検証";
Apromore.I18N.SyntaxChecker.name = "構文チェッカ";
Apromore.I18N.SyntaxChecker.desc = "構文チェック";
Apromore.I18N.SyntaxChecker.noErrors = "構文エラーはありません。";
Apromore.I18N.SyntaxChecker.invalid = "サーバーからの回答が無効です。";
Apromore.I18N.SyntaxChecker.checkingMessage = "確認中 ...";

if(!Apromore.I18N.ConfigurationExtension) Apromore.I18N.ConfigurationExtension = {};

Apromore.I18N.ConfigurationExtension.name = "構成";
Apromore.I18N.ConfigurationExtension.group = "構成";
Apromore.I18N.ConfigurationExtension.desc = "バリアントの表示・非表示";

if(!Apromore.I18N.SelectionExtension) Apromore.I18N.SelectionExtension = {};

Apromore.I18N.SelectionExtension.name = "選択";
Apromore.I18N.SelectionExtension.group = "構成";
Apromore.I18N.SelectionExtension.desc = "選択する...";

if(!Apromore.I18N.AnimationExtension) Apromore.I18N.AnimationExtension = {};

Apromore.I18N.AnimationExtension.name = "アニメーション";
Apromore.I18N.AnimationExtension.group = "構成";
Apromore.I18N.AnimationExtension.desc = "アニメーションのログ...";

if(!Apromore.I18N.Undo) Apromore.I18N.Undo = {};

Apromore.I18N.Undo.group = "元に戻す";
Apromore.I18N.Undo.undo = "元に戻す";
Apromore.I18N.Undo.undoDesc = "最後のアクションを元に戻す";
Apromore.I18N.Undo.redo = "やり直し";
Apromore.I18N.Undo.redoDesc = "最後に行った操作をやり直す";

if(!Apromore.I18N.View) Apromore.I18N.View = {};

Apromore.I18N.View.group = "ズーム";
Apromore.I18N.View.zoomIn = "ズームイン";
Apromore.I18N.View.zoomInDesc = "モデルにズームイン";
Apromore.I18N.View.zoomOut = "ズームアウト";
Apromore.I18N.View.zoomOutDesc = "モデルのズームアウト";
Apromore.I18N.View.zoomStandard = "ズームスタンダード";
Apromore.I18N.View.zoomStandardDesc = "標準レベルへのズーム";
Apromore.I18N.View.zoomFitToModel = "モデルへのズームフィット";
Apromore.I18N.View.zoomFitToModelDesc = "モデルのサイズに合わせてズーム";

if(!Apromore.I18N.Share) Apromore.I18N.Share = {};

Apromore.I18N.Share.group = "共有";
Apromore.I18N.Share.share = "共有";
Apromore.I18N.Share.shareDesc = "モデルの共有";
Apromore.I18N.Share.publish = "公開";
Apromore.I18N.Share.publishDesc = "モデルの公開";
Apromore.I18N.Share.unpublish = "非公開";
Apromore.I18N.Share.unpublishDesc = "モデルの非公開";

if(!Apromore.I18N.SimulationPanel) Apromore.I18N.SimulationPanel = {};

Apromore.I18N.SimulationPanel.group = "シミュレーション";
Apromore.I18N.SimulationPanel.toggleSimulationDrawer = "シミュレーションパネルの切り替え";
Apromore.I18N.SimulationPanel.toggleSimulationDrawerDesc = "シミュレーションパラメータパネルの切り替え";
Apromore.I18N.SimulationPanel.simulateModel = "モデルのシミュレーション";
Apromore.I18N.SimulationPanel.simulateModelDesc = "モデルのシミュレーション";

/** New Language Properties: 08.12.2008 */

Apromore.I18N.PropertyWindow.title = "プロパティ";

if(!Apromore.I18N.ShapeRepository) Apromore.I18N.ShapeRepository = {};
Apromore.I18N.ShapeRepository.title = "図形リポジトリ";

Apromore.I18N.Save.dialogDesciption = "名前、説明、コメントを入力してください。";
Apromore.I18N.Save.dialogLabelTitle = "タイトル";
Apromore.I18N.Save.dialogLabelDesc = "説明";
Apromore.I18N.Save.dialogLabelType = "タイプ";
Apromore.I18N.Save.dialogLabelComment = "修正コメント";

Ext.MessageBox.buttonText.yes = "はい";
Ext.MessageBox.buttonText.no = "いいえ";
Ext.MessageBox.buttonText.cancel = "キャンセル";
Ext.MessageBox.buttonText.ok = "OK";

if(!Apromore.I18N.Perspective) Apromore.I18N.Perspective = {};
Apromore.I18N.Perspective.no = "視点なし"
Apromore.I18N.Perspective.noTip = "現在の視点のアンロード"

/** New Language Properties: 09.05.2009 */
if(!Apromore.I18N.JSONImport) Apromore.I18N.JSONImport = {};

Apromore.I18N.JSONImport.title = " JSONインポート";
Apromore.I18N.JSONImport.wrongSS = "インポートしたステンシルセット({0})が、読み込んだステンシルセット({1})と一致しません。"

/** New Language Properties: 15.05.2009*/
if(!Apromore.I18N.SyntaxChecker.BPMN) Apromore.I18N.SyntaxChecker.BPMN={};
Apromore.I18N.SyntaxChecker.BPMN_NO_SOURCE = "エッジにはソースが必要です。";
Apromore.I18N.SyntaxChecker.BPMN_NO_TARGET = "エッジにはターゲットが必要です。";
Apromore.I18N.SyntaxChecker.BPMN_DIFFERENT_PROCESS = "ソースノードとターゲットノードが同じプロセスに含まれている必要があります。";
Apromore.I18N.SyntaxChecker.BPMN_SAME_PROCESS = "ソースノードとターゲットノードは、異なるプールに含まれている必要があります。";
Apromore.I18N.SyntaxChecker.BPMN_FLOWOBJECT_NOT_CONTAINED_IN_PROCESS = "フローオブジェクトは、プロセスに含まれている必要があります。";
Apromore.I18N.SyntaxChecker.BPMN_ENDEVENT_WITHOUT_INCOMING_CONTROL_FLOW = "エンドイベントには、入力されるシーケンスフローが必要です。";
Apromore.I18N.SyntaxChecker.BPMN_STARTEVENT_WITHOUT_OUTGOING_CONTROL_FLOW = "スタートイベントには、発信するシーケンスフローが必要です。";
Apromore.I18N.SyntaxChecker.BPMN_STARTEVENT_WITH_INCOMING_CONTROL_FLOW = "スタートイベントには、入力されるシーケンスフローがあってはなりません。";
Apromore.I18N.SyntaxChecker.BPMN_ATTACHEDINTERMEDIATEEVENT_WITH_INCOMING_CONTROL_FLOW = "添付された中間イベントには、入力されるシーケンスフローがあってはならない。";
Apromore.I18N.SyntaxChecker.BPMN_ATTACHEDINTERMEDIATEEVENT_WITHOUT_OUTGOING_CONTROL_FLOW = "接続された中間イベントは、正確に1つの出力シーケンスフローを持たなければなりません。";
Apromore.I18N.SyntaxChecker.BPMN_ENDEVENT_WITH_OUTGOING_CONTROL_FLOW = "エンドイベントには、送信シーケンスフローがあってはなりません。";
Apromore.I18N.SyntaxChecker.BPMN_EVENTBASEDGATEWAY_BADCONTINUATION = "イベントベースのゲートウェイには、ゲートウェイやサブプロセスが続いてはいけません。";
Apromore.I18N.SyntaxChecker.BPMN_NODE_NOT_ALLOWED = "ノードタイプは許可されていません。";

if(!Apromore.I18N.SyntaxChecker.IBPMN) Apromore.I18N.SyntaxChecker.IBPMN={};
Apromore.I18N.SyntaxChecker.IBPMN_NO_ROLE_SET = "インタラクションには、送信者と受信者の役割が設定されている必要があります。";
Apromore.I18N.SyntaxChecker.IBPMN_NO_INCOMING_SEQFLOW = "このノードには、入力されるシーケンスフローが必要です。";
Apromore.I18N.SyntaxChecker.IBPMN_NO_OUTGOING_SEQFLOW = "このノードは、発信シーケンスフローを持っている必要があります。";

if(!Apromore.I18N.SyntaxChecker.InteractionNet) Apromore.I18N.SyntaxChecker.InteractionNet={};
Apromore.I18N.SyntaxChecker.InteractionNet_SENDER_NOT_SET = "送信者が設定されていません";
Apromore.I18N.SyntaxChecker.InteractionNet_RECEIVER_NOT_SET = "レシーバーが設定されてません";
Apromore.I18N.SyntaxChecker.InteractionNet_MESSAGETYPE_NOT_SET = "メッセージタイプが設定されてません";
Apromore.I18N.SyntaxChecker.InteractionNet_ROLE_NOT_SET = "役割が設定されてません";

if(!Apromore.I18N.SyntaxChecker.EPC) Apromore.I18N.SyntaxChecker.EPC={};
Apromore.I18N.SyntaxChecker.EPC_NO_SOURCE = "各エッジにはソースが必要です。";
Apromore.I18N.SyntaxChecker.EPC_NO_TARGET = "各エッジにはターゲットが必要です。";
Apromore.I18N.SyntaxChecker.EPC_NOT_CONNECTED = "ノードはエッジで接続されている必要があります。";
Apromore.I18N.SyntaxChecker.EPC_NOT_CONNECTED_2 = "ノードは、より多くのエッジで接続されている必要があります。";
Apromore.I18N.SyntaxChecker.EPC_TOO_MANY_EDGES = "ノードの連結エッジ数が多すぎます。";
Apromore.I18N.SyntaxChecker.EPC_NO_CORRECT_CONNECTOR = "ノードが正しいコネクタではありません。";
Apromore.I18N.SyntaxChecker.EPC_MANY_STARTS = "スタートイベントは1つだけでなければなりません。";
Apromore.I18N.SyntaxChecker.EPC_FUNCTION_AFTER_OR = "分割したOR/XORの後に関数があってはいけません。";
Apromore.I18N.SyntaxChecker.EPC_PI_AFTER_OR = "分割したOR/XORの後には、プロセス・インターフェースがあってはなりません。";
Apromore.I18N.SyntaxChecker.EPC_FUNCTION_AFTER_FUNCTION =  "機能の後に機能があってはいけません。";
Apromore.I18N.SyntaxChecker.EPC_EVENT_AFTER_EVENT =  "イベントの後にイベントがあってはいけません。";
Apromore.I18N.SyntaxChecker.EPC_PI_AFTER_FUNCTION =  "関数の後にプロセス・インターフェースがあってはいけません。";
Apromore.I18N.SyntaxChecker.EPC_FUNCTION_AFTER_PI =  "プロセスインターフェースの後には、機能があってはいけません。";
Apromore.I18N.SyntaxChecker.EPC_SOURCE_EQUALS_TARGET = "エッジは2つの異なるノードを結ぶ必要があります。"

if(!Apromore.I18N.SyntaxChecker.PetriNet) Apromore.I18N.SyntaxChecker.PetriNet={};
Apromore.I18N.SyntaxChecker.PetriNet_NOT_BIPARTITE = "グラフが二分されていません";
Apromore.I18N.SyntaxChecker.PetriNet_NO_LABEL = "ラベル付きのトランジションにラベルが設定されてません";
Apromore.I18N.SyntaxChecker.PetriNet_NO_ID = " idを持たないノードがあります";
Apromore.I18N.SyntaxChecker.PetriNet_SAME_SOURCE_AND_TARGET = " 2つのフロー関係は同じソースとターゲットを持ちます";
Apromore.I18N.SyntaxChecker.PetriNet_NODE_NOT_SET = "フローの関係性にノードが設定されてません";

/** New Language Properties: 02.06.2009*/
Apromore.I18N.Edge = "エッジ";
Apromore.I18N.Node = "ノード";

/** New Language Properties: 03.06.2009*/
Apromore.I18N.SyntaxChecker.notice = "赤い十字のアイコンにマウスを合わせると、エラーメッセージが表示されます。";

/** New Language Properties: 05.06.2009*/
if(!Apromore.I18N.RESIZE) Apromore.I18N.RESIZE = {};
Apromore.I18N.RESIZE.tipGrow = "キャンバスサイズの拡大:";
Apromore.I18N.RESIZE.tipShrink = "キャンバスサイズの縮小:";
Apromore.I18N.RESIZE.N = "上";
Apromore.I18N.RESIZE.W = "左";
Apromore.I18N.RESIZE.S ="下";
Apromore.I18N.RESIZE.E ="右";

/** New Language Properties: 15.07.2009*/
if(!Apromore.I18N.Layouting) Apromore.I18N.Layouting ={};
Apromore.I18N.Layouting.doing = "レイアウト中...";

/** New Language Properties: 18.08.2009*/
Apromore.I18N.SyntaxChecker.MULT_ERRORS = "マルチプルエラー";

/** New Language Properties: 08.09.2009*/
if(!Apromore.I18N.PropertyWindow) Apromore.I18N.PropertyWindow = {};
Apromore.I18N.PropertyWindow.oftenUsed = "よく使われる";
Apromore.I18N.PropertyWindow.moreProps = "その他の物件";

/** New Language Properties 01.10.2009 */
if(!Apromore.I18N.SyntaxChecker.BPMN2) Apromore.I18N.SyntaxChecker.BPMN2 = {};

Apromore.I18N.SyntaxChecker.BPMN2_DATA_INPUT_WITH_INCOMING_DATA_ASSOCIATION = "データ入力は、入力されるデータ結合があってはいけません。";
Apromore.I18N.SyntaxChecker.BPMN2_DATA_OUTPUT_WITH_OUTGOING_DATA_ASSOCIATION = "データ出力には、出力されるデータ結合があってはいけません。";
Apromore.I18N.SyntaxChecker.BPMN2_EVENT_BASED_TARGET_WITH_TOO_MANY_INCOMING_SEQUENCE_FLOWS = "イベントベースのゲートウェイのターゲットは、受信するシーケンスフローを1つだけ持つことができる。";

/** New Language Properties 02.10.2009 */
Apromore.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WITH_TOO_LESS_OUTGOING_SEQUENCE_FLOWS = "イベントベースゲートウェイには、2つ以上の送信シーケンスフローが必要です。";
Apromore.I18N.SyntaxChecker.BPMN2_EVENT_BASED_EVENT_TARGET_CONTRADICTION = "メッセージ中間イベントを使用する場合は、受信タスクを使用してはいけませんし、その逆も同様です。";
Apromore.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WRONG_TRIGGER = "以下の中間イベントトリガーのみが有効です。メッセージ、シグナル、タイマー、条件付き、マルチプル。";
Apromore.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WRONG_CONDITION_EXPRESSION = "イベントゲートウェイの送出シーケンスフローには、条件式があってはいけません。";
Apromore.I18N.SyntaxChecker.BPMN2_EVENT_BASED_NOT_INSTANTIATING = "ゲートウェイが、プロセスをインスタンス化する条件を満たしていません。ゲートウェイには、開始イベントまたはインスタンス化属性を使用してください。";

/** New Language Properties 05.10.2009 */
Apromore.I18N.SyntaxChecker.BPMN2_GATEWAYDIRECTION_MIXED_FAILURE = "ゲートウェイには、複数の受信および送信シーケンスフローが必要です。";
Apromore.I18N.SyntaxChecker.BPMN2_GATEWAYDIRECTION_CONVERGING_FAILURE = "ゲートウェイは複数の受信シーケンスフローを持つ必要がありますが、複数の送信シーケンスフローを持つことはできません。";
Apromore.I18N.SyntaxChecker.BPMN2_GATEWAYDIRECTION_DIVERGING_FAILURE = "ゲートウェイは、複数の着信シーケンスフローを持ってはならないが、複数の発信シーケンスフローを持っていなければならない。";
Apromore.I18N.SyntaxChecker.BPMN2_GATEWAY_WITH_NO_OUTGOING_SEQUENCE_FLOW = "ゲートウェイには、最低1つの発信シーケンスフローが必要です。";
Apromore.I18N.SyntaxChecker.BPMN2_RECEIVE_TASK_WITH_ATTACHED_EVENT = "イベントゲートウェイ構成で使用される受信タスクは、添付された中間イベントを持ってはいけません。";
Apromore.I18N.SyntaxChecker.BPMN2_EVENT_SUBPROCESS_BAD_CONNECTION = "イベントサブプロセスは、流入または流出するシーケンスフローを有してはいけません。";

/** New Language Properties 13.10.2009 */
Apromore.I18N.SyntaxChecker.BPMN_MESSAGE_FLOW_NOT_CONNECTED = "メッセージフローの少なくとも片側が接続されている必要があります。";

/** New Language Properties 24.11.2009 */
Apromore.I18N.SyntaxChecker.BPMN2_TOO_MANY_INITIATING_MESSAGES = " Choreography Activityは、1つの開始メッセージしか持つことができません。";
Apromore.I18N.SyntaxChecker.BPMN_MESSAGE_FLOW_NOT_ALLOWED = "ここでは、メッセージフローは使用できません。";

/** New Language Properties 27.11.2009 */
Apromore.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WITH_TOO_LESS_INCOMING_SEQUENCE_FLOWS = "インスタンス化していないイベントベースゲートウェイは、最低1つの受信シーケンスフローを持たなければならない。";
Apromore.I18N.SyntaxChecker.BPMN2_TOO_FEW_INITIATING_PARTICIPANTS = "振り付けアクティビティには、開始する参加者（白）が1人必要です。";
Apromore.I18N.SyntaxChecker.BPMN2_TOO_MANY_INITIATING_PARTICIPANTS = "振付アクティビィティには、複数の開始参加者（白）がいてはいけません。"

Apromore.I18N.SyntaxChecker.COMMUNICATION_AT_LEAST_TWO_PARTICIPANTS = "通信は最低でも2人の参加者と接続する必要があります。";
Apromore.I18N.SyntaxChecker.MESSAGEFLOW_START_MUST_BE_PARTICIPANT = "メッセージフローの送信元は、参加者である必要があります。";
Apromore.I18N.SyntaxChecker.MESSAGEFLOW_END_MUST_BE_PARTICIPANT = "メッセージフローのターゲットは、参加者である必要があります。";
Apromore.I18N.SyntaxChecker.CONV_LINK_CANNOT_CONNECT_CONV_NODES = "会話リンクは、通信ノードまたはサブ会話ノードと参加者を接続する必要があります。";

// Migrated Apromore specific configuration

Apromore.I18N.PropertyWindow.dateFormat = " y/d/m ";

Apromore.I18N.View.East = "シミュレーションパラメータ";
Apromore.I18N.View.West = "モデリング要素";

Apromore.I18N.Apromore.title	= "Apromore.";
Apromore.I18N.Apromore.pleaseWait = "Apromoreのプロセスエディターが読み込まれるまでお待ちください。読み込み中です...";
Apromore.I18N.Edit.cutDesc = "選択範囲をクリップボードに切り出す";
Apromore.I18N.Edit.copyDesc = "選択範囲をクリップボードにコピーする";
Apromore.I18N.Edit.pasteDesc = "クリップボードの内容をキャンバスに貼り付ける";
Apromore.I18N.Save.pleaseWait = "お待ち下さい <br/>保存中...";

Apromore.I18N.Save.saveAs = "コピーを保存中...";
Apromore.I18N.Save.saveAsDesc = "コピーを保存中...";
Apromore.I18N.Save.saveAsTitle = "コピーを保存中...";
Apromore.I18N.Save.savedAs = "コピーを保存しました";
Apromore.I18N.Save.savedDescription = "プロセス図は、以下の場所に保存されます。";
Apromore.I18N.Save.notAuthorized = "現在、あなたはログインしていません。現在の図を保存するために、新しいウィンドウで<a href='/p/login' target='_blank'>ログイン</a>してください。"
Apromore.I18N.Save.transAborted = "保存リクエストに時間がかかりすぎました。より高速なインターネット回線を使用しても構いません。無線LANを使用している場合は、回線の強さを確認してください。";
Apromore.I18N.Save.noRights = "そのモデルを保存するために必要な権限がありません。<a href='/p/explorer' target='_blank'>Apromore.Explorer</a>で対象のディレクトリに書き込む権限が残っているかどうかを確認してください。";
Apromore.I18N.Save.comFailed = " Apromoreサーバーとの通信に失敗しました。インターネットの接続状況をご確認ください。問題が解決しない場合は、ツールバーの封筒マークからアプロモア・サポートにお問い合わせください。";
Apromore.I18N.Save.failed = "図の保存に失敗しました。もう一度お試しください。問題が解決しない場合は、Apromoreにお問い合わせください。ツールバーの封筒マークからサポートにお問い合わせください。";
Apromore.I18N.Save.exception = "図を保存しようとすると、いくつかの例外が発生します。もう一度お試しください。問題が解決しない場合は、Apromoreにお問い合わせください。ツールバーの封筒のマークからサポートにお問い合わせください。";
Apromore.I18N.Save.retrieveData = "データを取得中です。お待ちください。";

/** New Language Properties: 10.6.09*/
if(!Apromore.I18N.ShapeMenuPlugin) Apromore.I18N.ShapeMenuPlugin = {};
Apromore.I18N.ShapeMenuPlugin.morphMsg = "図形を変形";
Apromore.I18N.ShapeMenuPlugin.morphWarningTitleMsg = "図形を変形 ";
Apromore.I18N.ShapeMenuPlugin.morphWarningMsg = "変形した要素に含まれない子図形があります。<br/>変形させますか？";

/** New Language Properties: 08.09.2009*/
if(!Apromore.I18N.PropertyWindow) Apromore.I18N.PropertyWindow = {};
Apromore.I18N.PropertyWindow.oftenUsed = "主な特性";
Apromore.I18N.PropertyWindow.moreProps = "主な特性";

Apromore.I18N.PropertyWindow.btnOpen = "開く";
Apromore.I18N.PropertyWindow.btnRemove = "削除";
Apromore.I18N.PropertyWindow.btnEdit = "編集";
Apromore.I18N.PropertyWindow.btnUp = "上に移動";
Apromore.I18N.PropertyWindow.btnDown = "下に移動";
Apromore.I18N.PropertyWindow.createNew = "新規作成";

if(!Apromore.I18N.PropertyWindow) Apromore.I18N.PropertyWindow = {};
Apromore.I18N.PropertyWindow.oftenUsed = "主な属性";
Apromore.I18N.PropertyWindow.moreProps = "主な属性";
Apromore.I18N.PropertyWindow.characteristicNr = "コストとリソースの分析";
Apromore.I18N.PropertyWindow.meta = "カスタム属性";

if(!Apromore.I18N.PropertyWindow.Category){Apromore.I18N.PropertyWindow.Category = {}}
Apromore.I18N.PropertyWindow.Category.popular = "メイン属性";
Apromore.I18N.PropertyWindow.Category.characteristicnr = "コストとリソースの分析";
Apromore.I18N.PropertyWindow.Category.others = "その他属性";
Apromore.I18N.PropertyWindow.Category.meta = "カスタム属性";

if(!Apromore.I18N.PropertyWindow.ListView) Apromore.I18N.PropertyWindow.ListView = {};
Apromore.I18N.PropertyWindow.ListView.title = "編集: ";
Apromore.I18N.PropertyWindow.ListView.dataViewLabel = "すでにあるエントリー";
Apromore.I18N.PropertyWindow.ListView.dataViewEmptyText = "リストエントリーはありません。";
Apromore.I18N.PropertyWindow.ListView.addEntryLabel = "新規エントリーの追加";
Apromore.I18N.PropertyWindow.ListView.buttonAdd = "追加";
Apromore.I18N.PropertyWindow.ListView.save = "保存";
Apromore.I18N.PropertyWindow.ListView.cancel = "キャンセル";

if(!Apromore.I18N.Attachment) Apromore.I18N.Attachment = {};
Apromore.I18N.Attachment.attachment = "アタッチメント";
Apromore.I18N.Attachment.showDesc = "アタッチメントの表示";
Apromore.I18N.Attachment.hideDesc = "アタッチメントの非表示";
Apromore.I18N.Attachment.comment = "Comments";
Apromore.I18N.Attachment.showComments = "Show comments";
Apromore.I18N.Attachment.hideComments = "Hide comments";


if(!Apromore.I18N.FontSize) Apromore.I18N.FontSize = {};
Apromore.I18N.FontSize.fontSizeDesc= "Change font size";
Apromore.I18N.FontSize.fontsize ="Change Font Size";