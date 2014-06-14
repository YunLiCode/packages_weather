/**
 * 
 * Author: linsiran
 * Date: 2012-4-23下午04:04:03
 */
package com.lewa.weather.provider;

import android.content.Context;

/**
 * @author linsiran
 *
 */
public abstract class AbsInstanceAdapter {
	Context context;
	public AbsInstanceAdapter(Context context) {
		this.context = context;
	}
	
	public abstract int getCount();
	
	/**
	 * 365日历查询uri，segment为查询时间的毫秒数
	 * Author: linsiran
	 * Date: 2012-4-16下午04:52:48
	 */
	public abstract boolean loadLastSegment(String segment);
	
	/**
	 * 返回包名，便于365日历调用
	 * Author: linsiran
	 * Date: 2012-4-16下午04:54:26
	 */
	public String getPackageName(int position) {
		return context.getPackageName();
	}
	
	/**
	 * 365日历列表中该条目显示的主要内容
	 * Author: linsiran
	 * Date: 2012-4-16下午04:55:02
	 */
	public abstract String getContent(int position);
	
	/**
	 * 365日历列表中该条目显示的左下方文字内容
	 * Author: linsiran
	 * Date: 2012-4-16下午04:55:02
	 */
	public abstract String getSummary(int position);
	
	/**
	 * 365日历列表中该条目显示左下方圆形图标颜色
	 * Author: linsiran
	 * Date: 2012-4-16下午04:55:02
	 */
	public abstract String getIcon(int position);
	
	/**
	 * 365日历列表中，点击该条目时传递的参数
	 * Author: linsiran
	 * Date: 2012-4-16下午04:55:02
	 */
	public long getReferenceId(int position) {
		return 0;
	}
	
	/**
	 * 365日历列表中，该条目右下方显示的文字
	 * Author: linsiran
	 * Date: 2012-4-16下午05:01:31
	 */
	public String getRightContent(int position) {
		return null;
	}
	
	/**
	 * 365日历列表中，该条目右下方显示的图标
	 * Author: linsiran
	 * Date: 2012-4-16下午05:02:00
	 */
	public String getRightIcon(int position) {
		return null;
	}
	
	/**
	 * 365日历列表中显示该条目的时间
	 * 格式为：yyyy-MM-dd 09:00
	 * Author: linsiran
	 * Date: 2012-4-16下午05:02:16
	 */
	public abstract String getTime(int position);
}
