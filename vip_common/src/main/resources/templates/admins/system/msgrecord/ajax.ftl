<script type="text/javascript">

$(function(){
	$(".item_list").each(function(i){
        $(this).mouseover(function(){
            $(this).css("background","#fff8e1");
        }).mouseout(function(){
        	  $(this).css("background","#ffffff");
        });
    });
});
</script>
<table class="tb-list2" style="width:100%">
	<thead>
		<tr>
			<th style="width:150px;">用户信息</th>
			<th style="width:50px;">发送状态</th>
			<th>消息标题</th>
			<th style="width:150px;">接收手机</th>
			<th>接收邮箱</th>
			<th style="width: 400px;">消息内容</th>
		</tr>
	</thead>
		<#if dataList??>
				<tbody>
					<tr class="space">
						<td colspan="6">
						</td>
					</tr>
				</tbody>
			
				<#list dataList as list>
					<tbody>
						<tr class="hd">
							<td colspan="6">
								<span>创建时间：${list.addDate?string("yyyy-MM-dd HH:mm:ss")}</span>
								<span>创建IP：<a href="http://www.ip138.com/ips138.asp?ip=${list.sendIp }&action=2" target="_blank">${list.sendIp}</a></span>
							</td>
						</tr>
					</tbody>
				
					<tbody class="item_list" id="line_${list.id}">
						<tr>
							<td>
								<div class="txt">${list.userId?if_exists}<br/>
                                    <font id="text_${list.userId?if_exists}"></font>
									<#--<a href="javascript:showUser('${list.userId?if_exists}')" style="font-weight: bold;color:green;"><font id="text_${list.userId?if_exists}">${list.userName?if_exists}</font></a>-->
								</div>
							</td>
							<td>
								<#if list.sendStat == 0><font color="red">未发送</font></#if>
								<#if list.sendStat == 1><font color="orange">发送中</font></#if>
								<#if list.sendStat == 2>
									<font color="green">已成功</font>
									<br>
									<a href="javascript:reset('${list.id}');">重发</a>
								</#if>
								<#if list.sendStat == 3>
									<font color="grey">已失败</font>
                                    <br>
                                    <a href="javascript:reset('${list.id}');">重发</a>
								</#if>
							</td>
							<td>
								${list.title}
							</td>
							<td>
								${list.receivePhoneNumber?if_exists}
							</td>
							<td>
								${list.receiveEmail?if_exists}
							</td>
							<td>
								<#if !showCode>
									<#assign cont=list.cont?string>
									<#assign repindex=cont?index_of("验证码")>
									<#if repindex gte 0 >
										<#assign repc=cont?substring(repindex+4, repindex+11)>
									<#else>
										<#assign repindex=cont?index_of("SMS Code")>
										<#if repindex gte 0 >
											<#assign repc=cont?substring(repindex+9, repindex+16)>
										</#if>
									</#if>
									<#if repc??>
										${cont?replace(repc, "********")}
									<#else>
										${cont}
									</#if>
								<#else>
									 ${list.cont?if_exists}
								</#if>
							</td>
						</tr>
					</tbody>
				</#list>
				<tfoot>
					<tr>
						<td colspan="6">
							<div class="page_nav" id="pagin">
								<div class="con">
									<#if pager?? && pager=='' >共${itemCount }项</#if>
		                			${pager?if_exists}
								</div>
							</div>
						 </td>
					</tr>
				 </tfoot>
			<#else>
				<tbody class="air-tips">
					<tr>
						<td colspan="6">
							<p>没有符合要求的记录！</p>
						</td>
					</tr>
				</tbody>
			</#if>

</table>