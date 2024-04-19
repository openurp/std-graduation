[#ftl/]

<div style="display:none">
  <div id="manualAuditPrompt">
    [@b.form name="autoAuditForm_" method="post" action="!index" theme="list" onsubmit="return closeManualAudit();" target="contentDiv"]
      <input type="hidden" name="_params" value="${b.paramstring}"/>
      [@b.radios label="是否通过" name="result.passed" items="1:通过,0:不通过"/]
      [@b.textarea label="备注" name="result.comments"  maxlength="150" cols="40" rows="3" comment="(限150字)"/]
      [@b.formfoot]
        [@b.submit value="system.button.submit"/]
        [@b.reset value="system.button.reset" /]
      [/@]
    [/@]
  </div>
</div>
<script>
  function manualAuditPrompt(){
    var ids = bg.input.getCheckBoxValues("result.id");
    if(ids==null||ids==""){
      alert("请选择记录进行操作!");  return;
    }
    if(isPublished(ids)) {
      return;
    }
    var form = document.autoAuditForm_;
    bg.form.addInput(form, "resultIds", ids);
    jQuery.colorbox({transition : 'none', title : "手动设置审核结果", overlayClose : false, width:"700px", inline:true, href:"#manualAuditPrompt"});
  }

    function closeManualAudit() {
        jQuery.colorbox.close();
        return true;
    }
</script>
