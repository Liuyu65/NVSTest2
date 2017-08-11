package com.chendong.gank.library;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者：陈东  —  www.renwey.com
 * 日期：2016/12/1 - 18:01
 * 注释：
 */

/**
 * 作者：chendongde310
 * Github:www.github.com/chendongde310
 * 日期：2016/12/5 - 15:38
 * 注释：角标助手，处理全局消息计数模型
 * 更新内容：
 * 1.添加小红点
 */
public class SuperBadgeHelper implements Serializable, Cloneable {

    public static final int STYLE_DEFAULT = 1;
    public static final int STYLE_GONE = 0;
    public static final int STYLE_SMALL = 2;

    private String tag; //标签
    private View view; //寄生控件
    private int num; //计数
    private List<SuperBadgeHelper> paterBadge = new ArrayList<>();//关联的上级节点
    private List<SuperBadgeHelper> childBadge = new ArrayList<>();//关联的下级节点
    private Activity context; //控件所在页面
    private BadgeManger badge;//红点管理器
    private int style; // 数字样式
    private OnNumCallback onNumCallback;

    /**
     * @param context 当前Avtivity
     * @param view    绑定角标view
     * @param tag     用于绑定的唯一标记
     * @param num     角标数字
     * @param style   是否显示数字
     * @return SuperBadgeHelper
     */
    private SuperBadgeHelper(Activity context, View view, String tag, int num, int style) {
        if (SuperBadgeDater.getInstance().getBadge(tag) != null) {
            throw new IllegalArgumentException(tag + "标记已经被其他控件注册");
        }
        if (context == null) {
            throw new NullPointerException("context not is null ");
        }
        if (num < 0) {
            throw new IllegalArgumentException("初始化角标数字不能小于0");
        }
        if (tag == null) {
            throw new IllegalArgumentException("tag 不能为空");
        }

        this.tag = tag;
        this.view = view;
        this.num = num;
        this.context = context;
        this.style = style;

        badge = new BadgeManger(context);
        badge.setTargetView(view);
        badge.setBadgeStyle(style);
        paterAddNum(num);
    }

    public static SuperBadgeHelper init(Activity context, View view, String tag) {
        return init(context, view, tag, 0);
    }

    public static SuperBadgeHelper init(Activity context, View view, String tag, int num) {
        return init(context, view, tag, num, STYLE_DEFAULT);
    }


    /**
     * @param context 当前Avtivity
     * @param view    绑定角标view
     * @param tag     用于绑定的唯一标记
     * @param num     角标数字
     * @param style   显示样式
     * @return SuperBadgeHelper
     */
    public static SuperBadgeHelper init(Activity context, View view, String tag, int num, int style) {
        SuperBadgeHelper superBadge = SuperBadgeDater.getInstance().getBadge(tag);
        if (superBadge != null) {
            superBadge.setView(view);
            superBadge.setContext(context);
            superBadge.setBadgeStyle(style);
            superBadge.getBadge().setTargetView(view);
            if (superBadge.getStyle()) {
                superBadge.getBadge().setBadgeCount(superBadge.getNum());
            }
            return superBadge;
        } else {
            return new SuperBadgeHelper(context, view, tag, num, style);
        }
    }


    public static SuperBadgeHelper getSBHelper(String tag) {
        SuperBadgeHelper superBadge = SuperBadgeDater.getInstance().getBadge(tag);
        if (superBadge == null) {
            throw new NullPointerException("没有找到标记为[" + tag + "]的控件");
        }
        return superBadge;
    }


    @Deprecated
    public void setOnNumCallback(OnNumCallback onNumCallback) {
        this.onNumCallback = onNumCallback;
    }

    /**
     * 设置角标半径
     *
     * @param dipRadius 半径
     */
    public void setDipRadius(int dipRadius) {
        badge.setBackground(dipRadius, Color.parseColor("#d3321b"));
    }

    /**
     * 设置角标颜色
     *
     * @param badgeColor 颜色
     */
    public void setBadgeColor(int badgeColor) {
        badge.setBackground(9, badgeColor);
    }


    public void setBackground(int dipRadius, int badgeColor) {
        badge.setBackground(dipRadius, badgeColor);

    }

    /**
     * @return
     */
    public boolean getStyle() {
        return style != STYLE_GONE;
    }


    public  BadgeManger getBadge() {
        return badge;
    }

    /**
     * 控件添加数字
     *
     * @param i 添加数字大小
     */
    public void addNum(int i) {
        if (childBadge.size() >= 1) {
            throw new IllegalArgumentException("该控件不是根节点数据控件（该控件包含下级控件），无法完成添加操作");
        }
        paterAddNum(i);
    }


    private void paterAddNum(int i) {
        if (i < 0) {
            // throw new IllegalArgumentException("添加数字不能小于0");
        } else {
//            this.num = this.num + i;
            badge.setBadgeCount(i);
            SuperBadgeDater.getInstance().addBadge(this);
            //传递变化到上级控件
            for (SuperBadgeHelper bean : paterBadge) {
                if (bean != null) {
                    bean.paterAddNum(i);
                }
            }
        }
    }


    public Activity getContext() {
        return context;
    }

    private void setContext(Activity context) {
        this.context = context;
    }


    public String getTag() {
        return tag;
    }

    private void setTag(String tag) {
        this.tag = tag;
    }

    public View getView() {
        return view;
    }

    private void setView(View view) {
        this.view = view;
    }

    public int getNum() {
        return num;
    }

    /**
     * 读取所有消息，减去所有数字
     */
    public void read() {
        chlidLessNum(getNum());
    }

    /**
     * @param i 减少数字
     */
    private void chlidLessNum(int i) {
        if (i > 0) {

            badge.setBadgeCount(getNum() - i);

            this.num = num - i;
            SuperBadgeDater.getInstance().addBadge(this);
            changeBadge(i);
        }
    }

    /**
     * 减少
     *
     * @param i 减少数字
     */
    public void lessNum(int i) {
        if (childBadge.size() >= 1) {
            throw new IllegalArgumentException("该控件不是根节点数据控件（包含下级控件），无法完成减少操作");
        }
        chlidLessNum(i);
    }


    /**
     * 根据父级控件标签将他绑定到本级控件
     *
     * @param tag 父级控件的Tag
     */
    public void bindView(String tag) {

        for (SuperBadgeHelper pater : paterBadge) {
            if (pater.getTag().equals(tag)) {
                //  throw new IllegalArgumentException("不能重复添加相同控件");
                return;
            }
        }
        SuperBadgeHelper paterBadgeHelper = SuperBadgeDater.getInstance().getBadge(tag);
        if (paterBadgeHelper != null) {
            paterBadge.add(paterBadgeHelper); //添加本级父控件
            paterBadgeHelper.addChild(this);//添加到父级子控件
        } else {
            throw new NullPointerException("没有找到标记为[" + tag + "]的控件");
        }
    }


    public void bindView(SuperBadgeHelper pater) {
        bindView(pater.getTag());
    }


    private void addChild(SuperBadgeHelper superBadgeHelper) {
        if (superBadgeHelper != null) {
            childBadge.add(superBadgeHelper);
            paterAddNum(superBadgeHelper.getNum());
        }
    }


    /**
     * 传递关联的View
     *
     * @param num 减少的数字
     */
    private void changeBadge(int num) {

        if (num > 0) {
            //传递变化到上级控件
            for (SuperBadgeHelper bean : paterBadge) {
                if (bean != null && bean.getNum() != 0) {
                    bean.chlidLessNum(num);
                }
            }
        }

        //清空下级控件数字
        if (getNum() == 0) {
            if (childBadge.size() > 0) {
                for (SuperBadgeHelper bean : childBadge) {
                    if (bean != null) {
                        bean.read();
                    }
                }

            }
        }
    }

    /**
     * 角标样式
     *
     * @param style
     */
    public void setBadgeStyle(int style) {
        this.style = style;
        badge.setBadgeStyle(style);
    }

    /**
     * 为0时是否显示
     * @param mHideOnNull
     */
    public void setHideOnNull(boolean mHideOnNull) {
        badge.setHideOnNull(mHideOnNull);
    }


    public interface OnNumCallback {
        int lodingNum();//加载数字方法
    }


    public void setBadgeGravity(int gravity) {
        badge.setBadgeGravity(gravity);
    }


    public void setBadgeMargin(int dipMargin) {
        badge.setBadgeMargin(dipMargin);
    }

    public void setBadgeMargin(int leftDipMargin, int topDipMargin, int rightDipMargin, int bottomDipMargin) {
        badge.setBadgeMargin( leftDipMargin,  topDipMargin,  rightDipMargin,  bottomDipMargin);
    }




}
