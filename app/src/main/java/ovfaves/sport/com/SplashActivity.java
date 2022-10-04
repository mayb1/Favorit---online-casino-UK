package ovfaves.sport.com;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.appsflyer.AppsFlyerLib;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import io.michaelrocks.paranoid.Obfuscate;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
@Obfuscate
public class SplashActivity extends AppCompatActivity {

    private static final String APPSFLYER_ID = "iqu4KZGw8eqAEu85xnRnXD";

    private SharedPreferences save;
    private ValueCallback<Uri[]> uploadMessage = null;
    private String advertId = "", visitorId = "";
    private WebView webView;
    public static final int REQUEST_SELECT_FILE = 100;
    private static final String START_URL = "https://tb-int-site.pp.ua/";
    private static final String BUNDLE_ID = BuildConfig.APPLICATION_ID;
    private ProgressBar spinner;
    private boolean firstTime = true;
    private static final String LOG = "TESTLOG";


    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint({"SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        save = getSharedPreferences("save", MODE_PRIVATE);
        advertId = save.getString("adId", "");
        AsyncTask.execute(() -> {
            if (advertId.isEmpty()) {
                try {
                    AdvertisingIdClient.Info idInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());
                    advertId = idInfo.getId();
                    save.edit()
                            .putString("adId", advertId)
                            .apply();
                    setOnesignal(visitorId);
                    startHttpClient();
                } catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException | IOException | JSONException e) {
                    setOnesignal("");
                    e.printStackTrace();
                }
            } else {
                try {
                    setOnesignal(visitorId);
                    startHttpClient();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        setOnesignal(visitorId);
        initAppsflyer();

        webView = findViewById(R.id.webView);
        spinner = (ProgressBar)findViewById(R.id.progressBar);
        setupWebView(webView);

        AnalyticSingleton.getInstance(this).logEvent("open_web");

    }


    public void startHttpClient() throws JSONException {
        String apsUID = AppsFlyerLib.getInstance().getAppsFlyerUID(this);


        JSONObject jsonObject = new JSONObject();

        jsonObject.put("bundle_id" , BUNDLE_ID);
        jsonObject.put("appsflyer_device_id" , apsUID);
        jsonObject.put("advertising_id", advertId);


        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), jsonObject.toString());

        String buildUrl = START_URL + "api/user/check/v2/";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(buildUrl)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                changeActivity();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String serverAnswer = Objects.requireNonNull(response.body()).string();
                Log.d(LOG, "ServerAnswer ="+  serverAnswer);
                try {
                    JSONObject answerObj = new JSONObject(serverAnswer);

                    boolean user = answerObj.getBoolean("user");

                    if(!user)  {
                        OneSignal.setSubscription(false);
                        throw new IOException();
                    }

                    visitorId  = answerObj.getString("visitor_id");
                    startSecondHttpClient(visitorId);

                } catch (Exception e) { changeActivity(); }
            }
        });

    }

    public void startSecondHttpClient(String visitorId) throws  JSONException {

        OkHttpClient secondClient = new OkHttpClient();

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("bundle_id" , BUNDLE_ID);
        jsonObject.put("visitor_id" , visitorId);


        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), jsonObject.toString());


        Request secondRequest = new Request.Builder()
                .url(START_URL + "api/user/data/v2/")
                .post(body)
                .build();

        secondClient.newCall(secondRequest).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String secondAnswer = Objects.requireNonNull(response.body()).string();
                Log.d(LOG, secondAnswer);
                try {
                    JSONArray array = new JSONArray(secondAnswer);
                    JSONObject answerObj = array.getJSONObject(0);

                    String product_url = Objects.requireNonNull(answerObj).getString("product_url");
                    runOnUiThread(() -> webView.loadUrl(product_url));

                } catch (JSONException e) {
                    changeActivity();
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                changeActivity();
            }
        });

    }


    private void initAppsflyer(){
        AppsFlyerLib.getInstance().enableFacebookDeferredApplinks(false);
        AppsFlyerLib.getInstance().init(APPSFLYER_ID, null, this);
        AppsFlyerLib.getInstance().start(this);
    }


    private void setOnesignal(String advertId){
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.setExternalUserId(advertId);
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView(WebView webView){

        WebSettings webViewSettings = webView.getSettings();

        webViewSettings.setJavaScriptEnabled(true);
        String[] preparedUserAgentArray = webView.getSettings().getUserAgentString().split("\\)");
        StringBuilder userAgent = new StringBuilder();
        for(int i = 1 ; i < preparedUserAgentArray.length ; i++){
            userAgent.append(preparedUserAgentArray[i]);
        }
        webViewSettings.setUserAgentString(userAgent.toString());
        webViewSettings.setAllowContentAccess(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        CookieManager.getInstance().setAcceptCookie(true);
        webViewSettings.setSupportZoom(false);
        webViewSettings.setAppCacheEnabled(true);
        webViewSettings.setDomStorageEnabled(true);
        webViewSettings.setSavePassword(true);
        webViewSettings.setSaveFormData(true);
        webViewSettings.setDatabaseEnabled(true);
        webViewSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webViewSettings.setMixedContentMode(0);
        webViewSettings.setAllowFileAccess(true);
        webViewSettings.setAllowFileAccessFromFileURLs(true);
        webViewSettings.setAllowUniversalAccessFromFileURLs(true);
        webViewSettings.setUseWideViewPort(true);
        webViewSettings.setLoadWithOverviewMode(true);
        webViewSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webViewSettings.setLoadsImagesAutomatically(true);
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.setSaveEnabled(true);
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String s, String s1, String s2, String s3, long l) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(s));
                startActivity(i);
            }
        });

        WebChromeClient webChromeClient = new WebChromeClient(){
            @Override
            public boolean onShowFileChooser(WebView webView1, ValueCallback<Uri[]> filePathCallBack, FileChooserParams fileChooserParams){

                uploadMessage = filePathCallBack;
                Intent intent = fileChooserParams.createIntent();
                try{
                startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e) {
                    uploadMessage = null;
                    return false;
                }
                return true;
            }
        };

        webView.setWebChromeClient(webChromeClient);

        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                if(firstTime){
                    spinner.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                spinner.setVisibility(View.GONE);
                firstTime = false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){

                if(url.startsWith("tg:")){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setData(Uri.parse(url));
                    view.getContext().startActivity(intent);
                    return true;
                }

                if(url.startsWith("mailto")){
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("plain/text");
                    intent.putExtra(Intent.EXTRA_EMAIL, url.replace("mailto", ""));
                    view.getContext().startActivity(Intent.createChooser(intent, "Send email"));
                    return true;
                }

                if(url.startsWith("tel:")){
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(url));
                    view.getContext().startActivity(intent);
                    return true;
                }

                if(url.startsWith("https://diia.app")){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.getData().getAuthority();
                    view.getContext().startActivity(intent);
                    return true;
                }

                if(url.startsWith("https://t.me/joinchat")){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    view.getContext().startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_SELECT_FILE) {
            if (uploadMessage == null)
                return;
            uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
            uploadMessage = null;
        }
    }

    @Override
    public void onBackPressed(){
        if (webView.canGoBack()) {
            webView.goBack();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        AnalyticSingleton.getInstance(this).logActivity(this);
    }

    public void changeActivity(){
        Intent intent = new Intent(SplashActivity.this, GameActivity.class);
        startActivity(intent);
        finish();
    }
}