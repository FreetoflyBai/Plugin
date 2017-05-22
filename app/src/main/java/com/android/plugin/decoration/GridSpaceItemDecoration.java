package com.android.plugin.decoration;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Description: Item top 、bottom 、right 、left 都会偏移，留出相等间隙.
 *              如果只需要item bottom and right 留出间隙（正常line），
 *              请使用GridItemDecoration，并替换为空的drawable
 * Author     : kevin.bai
 * Time       : 2016/12/7 13:59
 * QQ         : 904869397@qq.com
 */

public class GridSpaceItemDecoration extends RecyclerView.ItemDecoration{

    public static final int VERTICAL_GRID= GridLayoutManager.VERTICAL;

    public static final int HORIZONTAL_GRID= GridLayoutManager.HORIZONTAL;

    private int mSpaceSize;

    private int mOrientation;

    private int[] HEADER_FOOTER_COUNT=new int[]{
            0,0
    };

    public GridSpaceItemDecoration(int spaceSize, int orientation){
        this.mSpaceSize=spaceSize;
        setOrientation(orientation);
    }

    /**
     * headerFooterCount 为Header 、Footer 数量, 数组形式
     * 默认为无Header 、Footer
     * @param spaceSize
     * @param orientation
     * @param headerFooterCount
     */
    public GridSpaceItemDecoration(int spaceSize, int orientation, int[] headerFooterCount){
        this.mSpaceSize=spaceSize;
        this.HEADER_FOOTER_COUNT=headerFooterCount;
        setOrientation(orientation);
    }

    private void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_GRID && orientation != VERTICAL_GRID) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    /**
     * 去除Header和Footer部分
     * 区分横向与纵向
     * @param spanCount
     * @param itemPosition
     * @param childCount
     * @return
     */
    private boolean getFirstRaw(int spanCount,int itemPosition,int childCount){
        if(itemPosition<HEADER_FOOTER_COUNT[0]){
            return false;
        }else if(itemPosition>=childCount-HEADER_FOOTER_COUNT[1]){
            return false;
        }else{
            itemPosition=itemPosition-HEADER_FOOTER_COUNT[0];
        }
        if(mOrientation== GridLayoutManager.VERTICAL){
            if(itemPosition<spanCount){
                return true;
            }
        }else{
            if(itemPosition%spanCount==0){
                return true;
            }
        }
        return false;
    }

    /**
     * 去除Header和Footer部分
     * 区分横向与纵向
     * @param spanCount
     * @param itemPosition
     * @param childCount
     * @return
     */
    private boolean getFirstColum(int spanCount,int itemPosition,int childCount){
        if(itemPosition<HEADER_FOOTER_COUNT[0]){
            return false;
        }else if(itemPosition>=childCount-HEADER_FOOTER_COUNT[1]){
            return false;
        }else{
            itemPosition=itemPosition-HEADER_FOOTER_COUNT[0];
        }
        if(mOrientation== GridLayoutManager.VERTICAL){
            if(itemPosition%spanCount==0){
                return true;
            }
        }else{
            if(itemPosition<spanCount){
                return true;
            }
        }
        return false;
    }

    /**
     * 首行top增加间距
     * 首列left增加间距
     * 其他right and bottom 增加间距
     * @param outRect
     * @param itemPosition
     * @param parent
     */
    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        GridLayoutManager gridLayoutManager= (GridLayoutManager) parent.getLayoutManager();
        int spanCount=gridLayoutManager.getSpanCount();
        int childCount=parent.getAdapter().getItemCount();
        outRect.set(
                getFirstColum(spanCount,itemPosition,childCount)?mSpaceSize:0,
                getFirstRaw(spanCount,itemPosition,childCount)?mSpaceSize:0,
                mSpaceSize,
                mSpaceSize);

    }
}