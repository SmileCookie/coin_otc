package com.world.util.string;

public class BranchPage {
	
	//当前index的位置
	private int curIndex = 3;
	
	//当前页面显示的条数  实际上是指xx*xx x的多少 所以显示的数据永远都是奇数
	private int pageSize = 2;
	
	//总页数
	private int count = 10;
	
	private String ellipsis = ",..." ;
	
	
	public BranchPage(int curIndex, int pageSize, int count) 
	{
		this.curIndex = curIndex;
		this.pageSize = pageSize;
		this.count = count;
	}


	
	
	/*****
	 * 
	 * 
	 * 
	 * @return String  组合好的字符传
	 */
	public String handlePage()
	{
		//页面
		StringBuffer sb = new StringBuffer();


		//当curIndex处于第一页
		if(curIndex==1)
		{	
			sb.append(",<1>");
			for(int i=0;i <pageSize ;i++)
			{
				int temp = curIndex+i+1;
				if(temp>=count)
				{//大于或者等于count要单独处理
					if(temp==count)
					{
						sb.append(","+temp);
					}
					ellipsis = "" ;
					break;
				}
				sb.append(","+temp);
			}
			sb.append(ellipsis);
		
		}
		else if(curIndex==count)
		{//当curIndex处于最后一页
			StringBuffer sbLast = new StringBuffer();
			
			for(int i=0;i <pageSize ;i++)
			{
				int temp = count-i-1;
				
				if(temp<=1)
				{
					if(temp==1)
					{
						ellipsis="";
						sbLast.insert(0,","+temp);
					}	
					break;
				}
				sbLast.insert(0,","+temp);
			}
			sbLast.insert(0, ellipsis);
			sb.append(sbLast.toString());
			sb.append(",<"+count+">");
			
		}
		else
		{//当curIndex处于其他的页
			String ellipsisLeft = ellipsis;
			String ellipsisRight = ellipsis;
			
			StringBuffer sbMiddle = new StringBuffer();
			sbMiddle.append(",<"+curIndex+">");
			for(int i=0;i<pageSize;i++)
			{
				int left = curIndex-i-1;
				
				int right = curIndex+i+1;

				//左边
				if(left<=1)
				{
					ellipsisLeft = "" ;
					if(left==1)
					{
						sbMiddle.insert(0,","+left);
					}
				}
				else
				{
					sbMiddle.insert(0,","+left);
				}
				
				//右边
				if(right>=count)
				{
					ellipsisRight = "";
					if(right==count)
					{
						sbMiddle.insert(sbMiddle.length(),","+right);
					}
				}
				else
				{
					sbMiddle.insert(sbMiddle.length(),","+right);
				}
			
			}
			sbMiddle.insert(0, ellipsisLeft);
			sbMiddle.insert(sbMiddle.length(), ellipsisRight);
			sb.append(sbMiddle.toString());
		}
		

		return sb.toString();
	}




	public int getCurIndex() {
		return curIndex;
	}

	public void setCurIndex(int curIndex) {
		this.curIndex = curIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getEllipsis() {
		return ellipsis;
	}

	public void setEllipsis(String ellipsis) {
		this.ellipsis = ellipsis;
	}
				
}
