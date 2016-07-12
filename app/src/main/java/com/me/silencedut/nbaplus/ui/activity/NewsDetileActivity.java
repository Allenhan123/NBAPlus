package com.me.silencedut.nbaplus.ui.activity;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.webkit.WebSettings;

import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.me.silencedut.nbaplus.R;
import com.me.silencedut.nbaplus.app.AppService;
import com.me.silencedut.nbaplus.data.Constant;
import com.me.silencedut.nbaplus.event.NewsDetailEvent;
import com.me.silencedut.nbaplus.utils.AppUtils;

import butterknife.Bind;

/**
 * Created by SilenceDut on 2015/12/8.
 */
public class NewsDetileActivity extends SwipeBackActivity{
    @Bind(R.id.webLayout)
    FrameLayout mWebLayout;
    WebView mWebView;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Intent mGetIntent;
    private ImageView mTitleImage;
    public static final String TITLE ="TITLE";
    public static final String DETILE_DATE="DETILE_DATE";
    public static final String DETILE_ID="DETILE_ID";
    public static final String IMAGE_URL="IMAGE_URL";
    public static final String IMAGE_EXIST="IMAGE_EXIST";


    @Override
    protected int getContentViewId() {
        return hasTitleImage()? R.layout.activity_detile : R.layout.activity_detile_noimage;
    }

    public void onEventMainThread(NewsDetailEvent event) {
        if(event!=null) {
            if(Constant.Result.FAIL.equals(event.getEventResult())) {
                AppUtils.showSnackBar(mSwipeBackLayout, R.string.load_fail);
                return;
            }
            mWebView.loadDataWithBaseURL(null,event.getContent(), "text/html", "UTF-8", null);
        }
    }


    private boolean hasTitleImage() {
        return getIntent().getBooleanExtra(IMAGE_EXIST, false);
    }

    @Override
    void setTitle() {
        mToolBar.setTitle(R.string.app_name);
    }
    @Override
    protected void initViews() {
        super.initViews();
        mGetIntent=getIntent();
        if(hasTitleImage()) {

            mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
            mCollapsingToolbarLayout.setTitle(mGetIntent.getStringExtra(TITLE));
            mTitleImage = (ImageView)findViewById(R.id.titleImage);
            mTitleImage.post(new Runnable() {
                @Override
                public void run() {
                    Glide.with(NewsDetileActivity.this).load(mGetIntent.getStringExtra(IMAGE_URL))
                    .placeholder(R.color.colorPrimary)
                    .into(mTitleImage);
                }
            });

        } else {
            mToolBar.setBackgroundResource(R.color.colorPrimary);

        }
        mWebView = new WebView(getApplicationContext());
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setBackgroundColor(0);
        mWebLayout.addView(mWebView);

        getNewsDetail();
    }

    private void getNewsDetail(){
        AppService.getInstance().getNewsDetail(getTaskId(), mGetIntent.getStringExtra(DETILE_DATE), mGetIntent.getStringExtra(DETILE_ID));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mWebLayout!=null) {
            mWebLayout.removeAllViews();
            mWebView.destroy();
        }
    }
}
