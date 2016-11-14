package edu.jnu.recyclerviewplus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.jnu.recyclerviewplus.adpter.BaseAdapter;
import edu.jnu.recyclerviewplus.adpter.RecyclerViewPlusAdapter;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.button)
    TextView button;
    @BindView(R.id.ptr_classic_frame)
    PtrClassicFrameLayout ptrClassicFrame;

    //布局的类型
    private int lmType = 1;

    private int page = 1;
    private int firstVisibleItem = 0;

    private List<String> list;
    private RecyclerViewPlusAdapter adapter;

    private View headerView;
    private View loadingView;
    private View loadAllView;
    private View footerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();

        ptrClassicFrame.post(new Runnable() {
            @Override
            public void run() {
                ptrClassicFrame.autoRefresh();
            }
        });
        ptrClassicFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return firstVisibleItem == 0 && PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {

                showData();
                hidePtyLayout();
            }
        });
    }

    private void init() {
        list = new ArrayList<>();
        for (int i = 0; i < Constant.images.length; i++)
            list.add(Constant.images[i]);
        headerView = LayoutInflater.from(this).inflate(R.layout.item_header, null);
        footerView = LayoutInflater.from(this).inflate(R.layout.item_footer, null);
        loadingView = this.getLayoutInflater().inflate(R.layout.load_more_custom, null);
        loadAllView = this.getLayoutInflater().inflate(R.layout.load_more_complete, null);
    }

    private void loadMore() {
        for (int i = 0; i < Constant.images.length; i++)
            list.add(Constant.images[i]);
        if (page == 4) {
            adapter.setLoadAll(true);
            //加载完成用Footer
            adapter.addFooterView(loadAllView);
            return;
        }
        showData();
    }

    private void showData() {
        if (adapter == null) {
            adapter = new RecyclerViewPlusAdapter(list);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(adapter);
            adapter.addHeaderView(headerView);
            adapter.setOnLoadMoreListener(new BaseAdapter.OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    page++;
                    loadMore();
                }
            });
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    firstVisibleItem = adapter.getFirstVisiblePosition(recyclerView.getLayoutManager());
                    Log.d("detaY", firstVisibleItem + "");
                }
            });
            adapter.addLoadingView(loadingView);
        }
        adapter.reset();
    }

    @OnClick(R.id.button)
    public void onClick() {
        changeLayoutManager();
    }

    private void changeLayoutManager() {
        lmType++;
        if (lmType > 3) lmType = 1;
        switch (lmType) {
            case 1:
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(adapter);
                break;
            case 2:
                GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
                recyclerView.setLayoutManager(gridLayoutManager);
                recyclerView.setAdapter(adapter);
                break;
            case 3:
                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(staggeredGridLayoutManager);
                recyclerView.setAdapter(adapter);
                break;
            default:
                break;
        }
    }

    private void hidePtyLayout() {
        if (ptrClassicFrame != null && ptrClassicFrame.isShown()) {
            ptrClassicFrame.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ptrClassicFrame.refreshComplete();
                }
            }, 500);
        }
    }

    static class ViewHolder {
        @BindView(R.id.header_title)
        TextView headerTitle;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
