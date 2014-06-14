/**
 * 
 * Author: linsiran
 * Date: 2012-4-23下午04:13:00
 */
package com.lewa.weather.provider;

import android.content.Context;

/**
 * @author linsiran
 *
 */
public abstract class AbsIconToolAdapter {
	Context context;
	
	public AbsIconToolAdapter(Context context) {
		this.context = context;
	}
	
	/**
	 * 365日历查询uri，segment为查询时间的毫秒数
	 * Author: linsiran
	 * Date: 2012-4-16下午04:52:48
	 */
	public abstract boolean loadLastSegment(String segment);
	
	/**
	 * 返回数据总数
	 * Author: linsiran
	 * Date: 2012-4-23下午04:42:49
	 */
	public abstract int getCount();
	
	/**
	 * 返回图片链接地址
	 * 当url为null时则通过Rsid取数据
	 * Author: linsiran
	 * Date: 2012-4-23下午04:41:13
	 */
	public abstract String getImgUrl(int position);
	
	/** 
	 * 返回图标右上角显示的内容
	 * Author: linsiran
	 * Date: 2012-4-23下午04:43:04
	 */
	public abstract String getName(int position);
	
	/**
	 * 返回包名
	 * Author: linsiran
	 * Date: 2012-4-23下午04:43:24
	 */
	public String getPackageName(int position) {
		return context.getPackageName();
	}
	
	/**
	 * 返回点击事件所需要进入的类的类名
	 * Author: linsiran
	 * Date: 2012-4-23下午04:43:57
	 */
	public abstract String getClassName(int position);
	
	
	/**
	 * 返回点击事件所需要传递的参数
	 * Author: linsiran
	 * Date: 2012-4-23下午04:44:20
	 */
	public abstract String getData(int position);
	
	/**
	 * 返回资源ID
	 * 当资源ID为0的时候则通过ImgUrl取图片
	 * Author: linsiran
	 * Date: 2012-4-23下午04:44:20
	 */
	public abstract int getRsid(int position);
}
