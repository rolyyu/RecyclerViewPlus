package edu.jnu.recyclerviewplus.adpter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by roly on 2016/10/28.
 * Header,Footer,LoadMore适配器
 */
public abstract class BaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "BaseAdapter";

    private static final int ITEM_LOAD_MORE = 0;
    private static final int ITEM_HEADER_VIEW = 1;
    private static final int ITEM_FOOTER_VIEW = 2;
    private static final int ITEM_CONTENT_VIEW = 3;

    private View headerView = null;
    private View footerView = null;
    private View loadView = null;

    private RecyclerView recyclerView;

    //是否显示加载中
    private boolean loading = false;

    //是否全部加载完成
    private boolean isLoadAll = false;

    protected abstract int getCount();

    protected abstract RecyclerView.ViewHolder onCreateView(ViewGroup parent, int viewType);

    protected abstract void onBindView(RecyclerView.ViewHolder holder, int position);

    private OnLoadMoreListener onLoadMoreListener;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    /**
     * 设置加载更多回调
     *
     * @param onLoadMoreListener
     */
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
        setLoadMoreListener();
    }

    /**
     * 设置点击事件
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 设置长按事件
     *
     * @param onItemLongClickListener
     */
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_HEADER_VIEW) {
            return new ViewHolder(headerView);
        } else if (viewType == ITEM_FOOTER_VIEW) {
            return new ViewHolder(footerView);
        } else if (viewType == ITEM_LOAD_MORE) {
            return new ViewHolder(loadView);
        } else {
            return onCreateView(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        final boolean isViewHolder = holder instanceof ViewHolder;

        if (headerView != null) {
            position = position - 1;
        }

        if (!isViewHolder) {
            onBindView(holder, position);
        }

        if (onItemClickListener != null && !isViewHolder) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getLayoutPosition();
                    if (headerView != null) {
                        pos = pos - 1;
                    }
                    onItemClickListener.onItemClick(view, pos);
                }
            });
        }

        if (onItemLongClickListener != null && !isViewHolder) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = holder.getLayoutPosition();
                    if (headerView != null) {
                        pos = pos - 1;
                    }
                    onItemLongClickListener.onItemLongClick(view, pos);
                    return false;
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        int count = getCount();
        if (count == 0) {
            return 0;
        }
        if (loading && loadView != null) {
            count++;
        }
        if (footerView != null) {
            count++;
        }
        if (headerView != null) {
            count++;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (headerView != null && position == 0) {
            return ITEM_HEADER_VIEW;
        }
        int lastPos = headerView == null ? getCount() : getCount() + 1;
        if (loadView != null && position >= lastPos && loading) {
            return ITEM_LOAD_MORE;
        }
        if (footerView != null && position >= lastPos) {
            return ITEM_FOOTER_VIEW;
        }
        return ITEM_CONTENT_VIEW;
    }

    /**
     * 网格布局Header、Footer、LoadMore 占一整行
     *
     * @param recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    RecyclerView.Adapter adapter = recyclerView.getAdapter();
                    if (isFullSpanType(adapter.getItemViewType(position))) {
                        return gridLayoutManager.getSpanCount();
                    }
                    return 1;
                }
            });
        }
    }

    /**
     * 瀑布流布局Header、Footer、LoadMore 占一整行
     *
     * @param holder
     */
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        int viewType = getItemViewType(position);
        if (isFullSpanType(viewType)) {
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
                lp.setFullSpan(true);
            }
        }
    }

    /**
     * 布局类型是Header、Footer、LoadMore
     *
     * @param type
     * @return
     */
    private boolean isFullSpanType(int type) {
        return type == ITEM_HEADER_VIEW || type == ITEM_FOOTER_VIEW || type == ITEM_LOAD_MORE;
    }


    /**
     * 添加Header
     *
     * @param view
     */
    public void addHeaderView(View view) {
        if (view == null) {
            throw new NullPointerException("HeadView is null");
        }
        if (headerView != null) {
            return;
        }
        headerView = view;
        headerView.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        notifyDataSetChanged();
    }

    /**
     * 移除Header
     */
    public void removeHeaderView() {
        if (headerView != null) {
            headerView = null;
            notifyDataSetChanged();
        }
    }

    /**
     * 添加Footer
     *
     * @param view
     */
    public void addFooterView(View view) {
        if (view == null) {
            throw new NullPointerException("FooterView is null!");
        }
        if (footerView != null) {
            return;
        }
        footerView = view;
        footerView.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        notifyItemInserted(getItemCount() - 1);
    }

    /**
     * 移除Footer
     */
    public void removeFooterView() {
        if (footerView != null) {
            footerView = null;
            notifyItemRemoved(getItemCount());
        }
    }

    /**
     * 添加LoadMore
     *
     * @param view
     */
    public void addLoadingView(View view) {
        if (view == null) {
            throw new NullPointerException("LoadingView is null!");
        }
        if (loadView != null) {
            return;
        }
        loadView = view;
        loadView.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
    }

    /**
     * 移除LoadMore
     */
    private void removeLoadingView() {
        loading = false;
        notifyDataSetChanged();
    }

    /**
     * 通知更新数据
     */
    public final void reset() {
        if (recyclerView != null) {
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    if (loading) {
                        removeLoadingView();
                    } else {
                        notifyDataSetChanged();
                    }
                    loading = false;
                }
            });
        }
    }

    /**
     * 设置是否已经全部加载完成
     *
     * @param loadAll
     */
    public final void setLoadAll(boolean loadAll) {
        if (loading) {
            reset();
        } else if (!loadAll) {
            footerView = null;
        }
        isLoadAll = loadAll;
    }

    private void setLoadMoreListener() {
        if (onLoadMoreListener != null) {
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    final int visibleItemCount = layoutManager.getChildCount();
                    final int totalItemCount = layoutManager.getItemCount();
                    Log.d(TAG, totalItemCount + " " + getLastVisiblePosition(layoutManager));
                    if (!loading && !isLoadAll && visibleItemCount > 0 && newState == RecyclerView.SCROLL_STATE_IDLE &&
                            (getLastVisiblePosition(layoutManager)) >= totalItemCount - 1) {
                        loading = true;
                        if (loadView != null) {
                            notifyItemInserted(getItemCount());
                        }
                        recyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onLoadMoreListener.onLoadMore();
                            }
                        }, 1000);
                    }
                }
            });
        }
    }

    /**
     * 获取最后一条展示索引
     *
     * @param layoutManager
     * @return
     */
    private int getLastVisiblePosition(RecyclerView.LayoutManager layoutManager) {
        int position;
        if (layoutManager instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager mlayoutManager = (StaggeredGridLayoutManager) layoutManager;
            int[] lastPositions = mlayoutManager.findLastVisibleItemPositions(new int[mlayoutManager.getSpanCount()]);
            position = getMaxPosition(lastPositions);
        } else {
            position = layoutManager.getItemCount() - 1;
        }
        return position;
    }

    /**
     * 获得最大的位置
     *
     * @param positions
     * @return
     */
    private int getMaxPosition(int[] positions) {
        int maxPosition = Integer.MIN_VALUE;
        for (int position : positions) {
            maxPosition = Math.max(maxPosition, position);
        }
        return maxPosition;
    }

    /**
     * 获取第一条展示的位置
     *
     * @return
     */
    public int getFirstVisiblePosition(RecyclerView.LayoutManager mLayoutManager) {
        int position;
        if (mLayoutManager instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
        } else if (mLayoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) mLayoutManager;
            int[] lastPositions = layoutManager.findFirstVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMinPositions(lastPositions);
        } else {
            position = 0;
        }
        return position;
    }

    /**
     * 获得当前展示最小的position
     *
     * @param positions
     * @return
     */
    private int getMinPositions(int[] positions) {
        int minPosition = Integer.MAX_VALUE;
        for (int position:positions) {
            minPosition = Math.min(minPosition, position);
        }
        return minPosition;
    }

    /**
     * Header、Footer、加载跟多 Holder
     */
    private class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
