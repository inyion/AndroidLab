package com.rel.csam.imagecatcher;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 소스를 보실지는 모르겠지만..
 * 그냥 심심해서 개인 프로젝트 한다고 생각하고 작업을 시작해서
 * 마침 라이브러리 사용 제약이 없다고 하시니
 * 유용한 라이브러리 뭐있나 찾아보고 사용해보면서 개발을 한거라
 * 라이브러리가 남용 되어있습니다
 * creator : sam
 * date : 2016. 11. 02.
 */
public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, ImageListener {

    private String mSite;
//    private ArrayList<String> mSlideImagePathList;
    private ArrayList<String> mThumbnailImagePathList;
//    private int mScreenSize;
//    private int mViewHeight;

    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private ImageListAdapter mAdapter;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSite = "https://www.gettyimagesgallery.com";

        mRefreshLayout = findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(this);

//        mTextView = (TextView) findViewById(R.id.html_text);

//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        WindowManager windowmanager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
//        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
//        mScreenSize = displayMetrics.widthPixels;

        mRecyclerView = findViewById(R.id.card_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 5);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ImageListAdapter(MainActivity.this, mThumbnailImagePathList);
        mRecyclerView.setAdapter(mAdapter);
        onRefresh();
    }

    @Override
    public void onRefresh() {
//        mSlideImagePathList = null;
        mThumbnailImagePathList = null;

        String page = "/collection/celebrities/";

        Observable.fromCallable(() -> {
            Connection.Response response = Jsoup.connect(mSite + page)
                    .method(Connection.Method.GET)
                    .execute();

            Document document = response.parse();

            String imgRegex = "(?i)<img[^>]+?src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>";
            Pattern p = Pattern.compile(imgRegex);

//            String slideImageHtmlStr = document.select("div#slider").toString();
            String mainImageStr = document.select("div[class=featured-image]").toString();
            String imagesStr = document.select("div[class=grid]").toString();
//            Matcher slideImageHtml = p.matcher(slideImageHtmlStr);
            Matcher mainImage = p.matcher(mainImageStr);
            Matcher images = p.matcher(imagesStr);

//            String slideImgs = "";
//            while(slideImageHtml.find()) {
//                if(mSlideImagePathList == null) {
//                    mSlideImagePathList = new ArrayList<>();
//                }
//
//                String imgSrc = slideImageHtml.group(1);
//                mSlideImagePathList.add(imgSrc);
//                slideImgs += imgSrc + "\n";
//            }

            String thumbnailImgs = "";
            while(mainImage.find()) {
                if(mThumbnailImagePathList == null) {
                    mThumbnailImagePathList = new ArrayList<>();
                }
                String imgSrc = mainImage.group(1);
                mThumbnailImagePathList.add(mSite + imgSrc);
                thumbnailImgs += imgSrc + "\n";
            }

            while(images.find()) {
                if(mThumbnailImagePathList == null) {
                    mThumbnailImagePathList = new ArrayList<>();
                }
                String imgSrc = mainImage.group(1);
                mThumbnailImagePathList.add(mSite + imgSrc);
                thumbnailImgs += imgSrc + "\n";
            }

            return false;
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe((result) -> {
            //Use result for something
//            mTextView.setText(thumbnailImageHtmlStr);
//            if (mSlideImagePathList != null && mSlideImagePathList.size() > 0) {
//                final CarouselView carouselView = (CarouselView) findViewById(R.id.carouselView);
//                carouselView.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        carouselView.setImageListener(MainActivity.this);
//                        carouselView.setPageCount(mSlideImagePathList.size());
//                        //                                    mViewHeight = carouselView.getMeasuredHeight();
//                    }
//                });
//            }

            if(mThumbnailImagePathList != null && mThumbnailImagePathList.size() > 0) {
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setImageList(mThumbnailImagePathList);
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }

            mRefreshLayout.setRefreshing(false);
        });
    }

    @Override
    public void setImageForPosition(int position, ImageView imageView) {
//        if (mSlideImagePathList != null && mSlideImagePathList.size() > position) {
//            Glide.with(this).load(mSite + mSlideImagePathList.get(position)).thumbnail(0.1f).into(imageView);
//        }
    }
}
