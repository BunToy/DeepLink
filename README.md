# DeepLink
BUNTOY support Mobile Deep Linking

Whether your Dapps are Web pages or Apps, BUNTOY now offers you and your users a convenient and reliable payment methods through deep linking.

Deep Linking is the method of launching a native APP through a link. In more detail, by mapping predefined behaviors to unique links, users can seamlessly jump to related content pages.

Deep Linking enables direct jumps to Web pages and Apps, and connects the App to the entire blockchain world.

At present, BUNTOY is free to open deeplink requests to everyone. You can contact us at any time and set up a seamless payment experience for your Dapps through the following SDK interface specification.

### Create a payment link

Call the interface to generate a payment jump link

[Url: http://myhero9.com/sdk/deepLinkUrl](http://myhero9.com/sdk/deepLinkUrl) , Use Post request

![](https://cdn-images-1.medium.com/max/1600/1*iL9bYGtoPh0DeK2hFacPBw.jpeg)

### Add the code as follows

```
String url = "https://lkme.cc/DOD/gJfVGGCfK";（）
Uri uri = Uri.parse(url);
Intent intent = new Intent(Intent.ACTION_VIEW, uri);
boolean flag = isAppInstallen(DemoActivity.this,"com.buntoy.wallet");
if(flag){
   intent.setClassName("com.buntoy.wallet","com.buntoy.wallet.activity.common.SplashActivity");
}
startActivity(intent);
```

### Follow us on:

[Twitter: https://twitter.com/buntoywallet](https://twitter.com/buntoywallet)

[Facebook: https://www.facebook.com/BuntoyWalletApp/](https://www.facebook.com/BuntoyWalletApp/)

[Discord: https://discord.gg/rDHBb4q](https://discord.gg/rDHBb4q)

[Telegram: https://t.me/joinchat/F9E5Iwq7C7mV0_gqFDPjVQ]( https://t.me/joinchat/F9E5Iwq7C7mV0_gqFDPjVQ)
