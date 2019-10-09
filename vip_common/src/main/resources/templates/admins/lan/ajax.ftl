
<script type="text/javascript">

	var jsArray = new Array();
	
	var keys = "";
	
	//获取单个
	function get(div){
		var id = $("#id").val('');
		var key = $("#key_"+div).val();
		$("#retBtn").hide();
		if(key == "") return;
		if(keys == key) return;
		$.getJSON("/admin/lan/get?key="+encodeURI(encodeURI(key, 'utf-8'), 'utf-8'), function(json){
			if(json.isSuc){
				$("#value_"+div).parent().find("span").remove();
				jsobj = json.datas; 
				jsArray[0] = json.datas;
				var html = "<span><i style='color:#317ee7'>"+json.datas.key+"</i>&nbsp;&nbsp;<input type=\"button\" onclick=\"showEdit(0, 'save')\"  value='点击修改' style='height:25px;cursor:pointer;'/></span>";
				$("#value_"+div).hide();
				$("#value_"+div).after(html);
				keys = key;
			}else{
				$("#value_"+div).val('')
				$("#value_"+div).show();
				$("#value_"+div).parent().find("span").remove();
			}
		});
	}
	
	//获取多个(10个)
	function like(){
		var id = $("#id").val('');
		retKey = '';
		var key = $("#key").val();
		if(key == ""){
			$("#value").parent().find("span").remove();
		 	return;
		}
		$.getJSON("/admin/lan/like?key="+encodeURI(encodeURI(key, 'utf-8'), 'utf-8'), function(json){
			if(json.isSuc){
				$("#value").parent().find("span").remove();
				var arr = json.datas;
				jsArray = arr;
				if(arr.length > 0){
					var html = '<span><i>最新10条记录：</i><br/>';
					for(var i = 0; i < arr.length; i++){
						html += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+(i+1)+"、<i style='color:#317ee7'>"+arr[i].key+"</i>&nbsp;&nbsp;<input type=\"button\" onclick=\"showEdit("+i+")\"  value='点击修改' style='height:25px;cursor:pointer;'/><br/>";
					}
					html += "</span>";
					$("#value").hide();
					$("#value").after(html);
				}else{
					$("#value").val('')
					$("#value").show();
					$("#value").parent().find("span").remove();
				}
			}else{
				$("#value").val('')
				$("#value").show();
				$("#value").parent().find("span").remove();
			}
		});
	}
	
	//提交
	function submit(){
		var id = $("#id").val();
		var key = $("#key").val();
		var content = $("#value").val();
		var tab = $("#tab").val();
		if(key == "" || content == "") return;
		vip.ajax({formId : "admin_user_update" , url : "/admin/lan/submit" , div : "admin_user_update" , suc : function(xml){
			if($(xml).find("State") == "true"){
				alert($(xml).find("Des").text());
				vip.list.reload();
				$("#id").val("");
			}else{
				alert($(xml).find("Des").text());
			}
		}});
		
	}
	
	var retKey = "";
	
	function showEdit(i){
		idx = 0;
	
		var jsobj = jsArray[i];
	
		var tab = $("#tab").val();
		if(tab == "cn")
			$("#value").val(jsobj.cnValue);
		else
			$("#value").val(jsobj.enValue);
		
		$("#value").show();
		$("#value").parent().find("span").hide();
		
		retKey = $("#key").val();
		
		
		$("#key").val(jsobj.key);
		$("#id").val(jsobj.id);
		
		$("#retBtn").show();
	}
	
	function reset(){
		$("#value").val('');
		$("#value").hide();
		$("#value").parent().find("span").show();
		$("#key").val(retKey);
		$("#id").val('');
		$("#retBtn").hide();
	}
	
	function check(lan){
		$.getJSON("/admin/lan/check?lan="+lan, function(json){
			if(json.isSuc){
				var data = json.datas;
				var snum = data[0];
				var unum = data[1];
				if(snum > 0){
					$("#snum").text(snum);
					$("#sbtn").show();
				}
				
				if(unum > 0){
					$("#unum").text(unum);
					$("#ubtn").show();
				}
			}else{
				$("#sbtn").hide();
				$("#ubtn").hide();
			}
		});
	}
	
	var dealing = 0;
	
	function imp(lan, tp){
		if(dealing == 1){
			alert("正在处理，请稍后");
			return;
		}
		dealing = 1;
		$.getJSON("/admin/lan/imp?lan="+lan+"&tp="+tp, function(json){
			alert(json.des);
			vip.list.reload();
		});
	}
</script>

<div id="admin_user_update" class="main-bd">
<input type="hidden" name="tab" id="tab" value="${tab }"/>
<input type="hidden" name="id" id="id" value=""/>
<table class="tb-list2" style="width:100%">
		<tr>
			<th colspan="2">
				<table class="tb-list2" style="width:100%">
					<tr>
						<th>可从TXT文本新增</th>
						<th>可从TXT文本修改</th>
						<th>操作</th>
					</tr>
					<tr>
						<td>
							<i id="snum">0</i>
						</td>
						<td>
							<i id="unum">0</i>
						</td>
						<td>
							<input type="button" onclick="check('${tab }')"  value="检 测" style="width:25%;height:30px;cursor:pointer;"/>
							<input type="button" onclick="imp('${tab }', 'add')" id="sbtn"  value="添 加" style="width:25%;height:30px;cursor:pointer;display:none;"/>
							<input type="button" onclick="imp('${tab }', 'update')" id="ubtn"  value="修 改" style="width:25%;height:30px;cursor:pointer;display:none;"/>
						</td>
					</tr>
				</table>
			</th>
		</tr>
		<tr>
			<th style="width:40%">翻译本体</th>
			<th>翻译内容</th>
		</tr>
		<tr>
			<td>
				<textarea style="width:90%;height:400px;" id="key" name="key" onkeyup="like()" onblur="like()"></textarea>
			</th>
			<td style="text-align:left;">
				<textarea style="width:98%;height:400px;" id="value" name="value"></textarea>
			</td>
		</tr>
		<tr>
			<th colspan="2" style="text-align:center;">
				<input type="button" onclick="submit()"  value="确 定" style="width:20%;height:30px;cursor:pointer;"/>
				&nbsp;&nbsp;
				<input type="button" onclick="reset()"  value="撤 回" id="retBtn" style="width:20%;height:30px;cursor:pointer;display:none;"/>
			</th>
		</tr>
</table>
</div>