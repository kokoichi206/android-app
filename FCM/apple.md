## 全然通知送れねーー！！

公式ドキュメント『[Apple プラットフォームで Firebase Cloud Messaging クライアント アプリを設定する](https://firebase.google.com/docs/cloud-messaging/ios/client?hl=ja)』

### [I solved this problem in 3 days. You need to connect the p8 key to your firebase](https://stackoverflow.com/questions/58246620/apns-device-token-not-set-before-retrieving-fcm-token-for-sender-id-react-nati)

Go to xCode and click on the "Capability" tab. Add "Background Modes" and "Push Notifications".
In the Background Modes tab, enable "Background fetch" and "Remote notifications"

Don't forget to add it for release, debug, profile


### Code 部分

[Integrate push notification using Firebase messaging in swift - iOS swift tutorial](https://iostutorialjunction.com/2021/11/integrate-push-notification-using-firebase-messaging-in-swift-ios-swift-tutorial.html)

