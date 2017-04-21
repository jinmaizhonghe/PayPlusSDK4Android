# PayPlusSDK【Android】
      PayPlusSDK是由金麦众合提供的聚合微信和支付宝的SDK，PayPlusSDK大大简化了使用者的接入流程，接入方式更加便捷高效，方便使用者更加关注业务。

## 版本要求
      Android5.0以上都支持

## 接入说明 
    （1）将PayPlusSDK、微信SDK、支付宝SDK添加到项目的lib目录下，并引入项目中。

    （2）权限说明
        <uses-permission android:name="android.permission.INTERNET" />
        <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
        <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
        <uses-permission android:name="android.permission.READ_PHONE_STATE" />
        <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    （3）注册activity
        <!-- 微信支付 -->
        <activity
          android:name="g3.yeepay.com.qianmaisdk.weixinPay.WXpayCallbackActivity"
          android:configChanges="orientation|keyboardHidden|navigation|screenSize"
          android:launchMode="singleTop"
          android:theme="@android:style/Theme.Translucent.NoTitleBar" />

       <activity-alias
          android:name=".wxapi.WXPayEntryActivity"
          android:exported="true"
          android:targetActivity="g3.yeepay.com.qianmaisdk.weixinPay.WXpayCallbackActivity" />

       <!--支付宝支付-->
       <activity
          android:name="com.alipay.sdk.app.H5PayActivity"
          android:configChanges="orientation|keyboardHidden|navigation"
          android:exported="false"
          android:screenOrientation="behind" >
       </activity>
       <activity
          android:name="com.alipay.sdk.auth.AuthActivity"
          android:configChanges="orientation|keyboardHidden|navigation"
          android:exported="false"
          android:screenOrientation="behind" >
       </activity>

    (4)发起支付
       4.1微信支付
       /**
        * 微信支付
        * @param pay_param 支付服务生成的支付参数
        */
        private void doWXPay(String pay_params){
        String wx_appid=“”;//替换为自己的appid
        WXpay.init(getApplicationContext(),wx_appid);//支付前调用
        WXpay.getInstance().doPay(pay_params, new WXpay.WXPayResultCallBack() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplication(),"支付成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int error_code) {
                switch (error_code) {
                   case WXpay.NO_OR_LOW_WX:
                           Toast.makeText(getApplication(), "未安装微信或微信版本过低", Toast.LENGTH_SHORT).show();
                       break;

                   case WXpay.ERROR_PAY_PARAM:
                           Toast.makeText(getApplication(), "参数错误", Toast.LENGTH_SHORT).show();
                       break;

                   case WXpay.ERROR_PAY:
                           Toast.makeText(getApplication(), "支付失败", Toast.LENGTH_SHORT).show();
                       break;
                }
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplication(), "支付取消", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
     4.2支付宝支付
     /**
     * 支付宝支付
     * @param pay_param 支付服务生成的支付参数
     */
     private void doAliPay(String pay_params){
     new Alipay(this, pay_params, new Alipay.AlipayResultCallBack() {
         @Override
         public void onSuccess() {
             Toast.makeText(getApplication(), "支付成功", Toast.LENGTH_SHORT).show();
         }

         @Override
         public void onDealing() {
             Toast.makeText(getApplication(), "支付处理中...", Toast.LENGTH_SHORT).show();
         }

         @Override
         public void onError(int error_code) {
             switch (error_code) {
                 case Alipay.ERROR_RESULT:
                     Toast.makeText(getApplication(), "支付失败:支付结果解析错误", Toast.LENGTH_SHORT).show();
                     break;

                 case Alipay.ERROR_NETWORK:
                     Toast.makeText(getApplication(), "支付失败:网络连接错误", Toast.LENGTH_SHORT).show();
                     break;

                 case Alipay.ERROR_PAY:
                     Toast.makeText(getApplication(), "支付错误:支付码支付失败", Toast.LENGTH_SHORT).show();
                     break;

                 default:
                     Toast.makeText(getApplication(), "支付错误", Toast.LENGTH_SHORT).show();
                     break;
             }
         }

         @Override
         public void onCancel() {
            Toast.makeText(getApplication(), "支付取消", Toast.LENGTH_SHORT).show();
         }
     }).doPay();
     }

## 常见问题说明：
    （1）微信支付不能吊起支付
        查看项目的包名和签名和在微信创建的应用是否一致，微信会检查包名和签名的一致性，相同才会吊起支付。
    （2）微信支付返回参数错误
        检查在服务器下单后返回的参数是否满足要求。
