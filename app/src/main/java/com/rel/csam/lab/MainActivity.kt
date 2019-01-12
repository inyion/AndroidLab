package com.rel.csam.lab

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.rel.csam.lab.model.LinkImage
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.util.*
import kotlin.collections.ArrayList

/**
 * 소스를 보실지는 모르겠지만..
 * 그냥 심심해서 개인 프로젝트 한다고 생각하고 작업을 시작해서
 * 마침 라이브러리 사용 제약이 없다고 하시니
 * 유용한 라이브러리 뭐있나 찾아보고 사용해보면서 개발을 한거라
 * 라이브러리가 남용 되어있습니다
 * creator : sam
 * date : 2016. 11. 02.
 */
class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    private val TAG: String = "Main"
    private val compositeDisposable = CompositeDisposable()
    private val mSite: String = "https://www.gettyimagesgallery.com/collection/celebrities/"
    private var mDataList: ArrayList<LinkImage> = ArrayList()
    private var mUrlList: ArrayList<String> = ArrayList()
    //    private int mScreenSize;
    //    private int mViewHeight;

    private var mRefreshLayout: SwipeRefreshLayout? = null
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: ImageListAdapter? = null
    //    private TextView mTextView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mRefreshLayout = findViewById(R.id.refresh_layout)
        mRefreshLayout!!.setOnRefreshListener(this)
        //        DisplayMetrics displayMetrics = new DisplayMetrics();
        //        WindowManager windowmanager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        //        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        //        mScreenSize = displayMetrics.widthPixels;

        mRecyclerView = findViewById(R.id.card_recycler_view)
        mRecyclerView!!.setHasFixedSize(true)
        mAdapter = ImageListAdapter(this@MainActivity, mDataList)
        mRecyclerView!!.adapter = mAdapter
        mUrlList.add(mSite)
        onRefresh()
    }

    override fun onBackPressed() {
        if (mUrlList.size > 0) {
            val lastIndex = mUrlList.size - 1
            getImageToLink(mUrlList[lastIndex])
            mUrlList.removeAt(lastIndex)
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    override fun onRefresh() {
        getImageToLink(mSite)
    }

    fun getImageToLink(url: String) {
        mDataList.clear()
        if (!url.equals(mSite)) {
            mUrlList.add(mSite)
        }

        val disposable = Observable.fromCallable {
            Log.i(TAG, "fromCallable")
            val response = Jsoup.connect(url)
                    .method(Connection.Method.GET)
                    .execute()
            Log.i(TAG, "execute")
            val document = response.parse()
            Log.i(TAG, "parse")
//            val imgRegex = "(?i)<img[^>]+?src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>"
//            val p = Pattern.compile(imgRegex)
//            val imgRegex = "img[src~=(?i)\\\\.(png|jpe?g|gif)]"
            val images = document.select("img")
            Log.i(TAG, "select img")
            for (image in images) {
                if (image.parentNode() != null) {
                    val parentNode = image.parentNode().parentNode()
                    if (image.hasAttr("data-zoomable") || (parentNode != null && parentNode.nodeName() == "a")) {
                        val data = LinkImage()
                        data.url = parentNode.attr("href")
                        data.image = image.attr("src")
                        mDataList.add(data)
                    }
                }
            }
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            if (mDataList.size > 0) {

                if(mDataList.size > 1) {
                    val layoutManager = GridLayoutManager(applicationContext, 3)
                    mRecyclerView!!.layoutManager = layoutManager
                } else {
                    val layoutManager = GridLayoutManager(applicationContext, 1)
                    mRecyclerView!!.layoutManager = layoutManager
                }

                mAdapter!!.setImageList(mDataList)
                mAdapter!!.notifyDataSetChanged()
            }

            mRefreshLayout!!.isRefreshing = false
        }
        compositeDisposable.add(disposable)
    }
}
