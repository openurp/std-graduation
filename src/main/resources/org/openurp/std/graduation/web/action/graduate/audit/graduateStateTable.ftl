[#ftl/]
<div style="display:none">
  <div id="applyGraduateStatusSetting">
    [@b.form name="applyGraduateStatusForm_" method="post" action="!index" target="contentDiv"]
    <input type="hidden" name="_params" value="${b.paramstring}"/>
         <table width="100%" align="center" class="grid-table">
            <tr height="35px">
                <td class="title" width="30%">学籍状态： </td>
                <td>
                <select name="stdStatus" style="width:160px;">
                  <option value="">...</option>
                  [#list stdStatuses! as stdStatus]
                  <option value="${stdStatus.id}">${stdStatus.name}</option>
                  [/#list]
                </select>
                </td>
            </tr>
        </table>
        <table width="100%" align="center">
            <tr height="35px">
                <td colspan="2" align="center">
                  <input type="button" value="${b.text("action.submit")}" onclick="applyGraduateStatus();" class="buttonStyle"/>
                </td>
            </tr>
        </table>
    [/@]
  </div>
</div>
<script>
  function applyGraduateStateSetting(){
       var ids = bg.input.getCheckBoxValues("result.id");
    if(ids==null||ids==""){
      alert("请选择记录进行操作!");  return;
    }
    if(!hasGradudateConfirmed(ids,true))
      return;
    ids=getStdIds(ids);
    var form=document.applyGraduateStatusForm_;
    bg.form.addInput(form, "studentIds", ids);
    if(hasGradudateState(ids, true)){
      jQuery.colorbox({transition : 'none', title : "应用毕业审核毕结业状态", overlayClose : false, width:"500px", inline:true, href:"#applyGraduateStatusSetting"});
    }
    }
    function applyGraduateStatus() {
    var form=document.applyGraduateStatusForm_;
    if(!jQuery(":input[name=stdStatus]").find('option:selected').val()){
      alert("请选择学籍状态！");
      return;
    }
    if(confirm("确定将当前所选学生的毕业状态，应用到学生的“学生学籍”中吗？")){
      bg.form.submit(form,"${b.base}/audit/graduate!applyGraduateState.action","contentDiv");
      jQuery.colorbox.close();
    }
    }
</script>
