package com.example.finnal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;

public class MainActivity extends AppCompatActivity
implements OnGetPoiSearchResultListener{
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private Button mbtnNormal = null;
    private Button mbtnSatellite = null;
    private Button btnSearch = null;
    private Button btnNews = null;
    private Button btnLocate = null;
    private EditText etSearchKeyword = null;
    private PoiSearch mPoiSearch = null;
    private LocationClient mLocationClient = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        initButton();

        // 初始化POI搜索模块
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);

        // 获取搜索输入框和搜索按钮的引用
        etSearchKeyword = findViewById(R.id.etSearchKeyword);
        btnSearch = findViewById(R.id.btnSearch);

        // 设置搜索按钮的点击事件
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = etSearchKeyword.getText().toString();
                if(!keyword.isEmpty()){
                    // 发起POI城市内检索
                    mPoiSearch.searchInCity(new PoiCitySearchOption()
                            .city("北京")
                            .keyword("美食")
                            .pageNum(0));
                }
            }
        });
    }




    // POI搜索结果回调
    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        if (poiResult == null ) {
            // 搜索错误或者未找到结果
            Toast.makeText(MainActivity.this, "未找到结果", Toast.LENGTH_LONG).show();
            return;
        }
        if (poiResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Log.v("有错误", String.valueOf(poiResult.error));
            return;
        }
        // 清除地图上的所有覆盖物
        mBaiduMap.clear();
        // 处理搜索结果
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_mark);

        // 遍历所有POI
        for (PoiInfo poi : poiResult.getAllPoi()){
            if(poi.location == null ) continue;

            MarkerOptions markerOptions = new MarkerOptions().position(poi.location)
                    .icon(bitmap);

            mBaiduMap.addOverlay(markerOptions);
        }

        // 将视图中心移动到第一个POI位置
        if (poiResult.getAllPoi() != null && poiResult.getAllPoi().size() > 0){
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(poiResult.getAllPoi().get(0).location));
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
        // 处理详细结果
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
        // 处理详细结果
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    private void initButton(){
        mbtnNormal = findViewById(R.id.btnNormal);
        mbtnNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
            }
        });
        mbtnSatellite = findViewById(R.id.btnSatellite);
        mbtnSatellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
            }
        });
        btnLocate = findViewById(R.id.btnLocate);
        btnLocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LocateActivity.class);
                startActivity(intent);
            }
        });
        btnNews = findViewById(R.id.btnNews);
        btnNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://news.baidu.com"));
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        if (mPoiSearch != null) {
            mPoiSearch.destroy(); // 销毁POI搜索模块
        }
    }
}