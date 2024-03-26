[#ftl]
[@b.head/]
[#macro panel title]
<div class="panel panel-default">
  <div class="panel-heading">
    <h3 class="panel-title">${title}</h3>
  </div>
  [#nested/]
</div>
[/#macro]

<div class="container" style="width:95%">

<nav class="navbar navbar-default" role="navigation">
  <div class="container-fluid">
    <div class="navbar-header">
        <a class="navbar-brand" href="#"><i class="fa-solid fa-graduation-cap"></i>学位申请</a>
    </div>
  </div>
</nav>
  [#if apply??]
  [@b.form name="removeApplyForm_"+apply.id  action="!remove?id="+apply.id+"&_method=delete"][/@]
  [#assign title]
     <i class="fa-solid fa-graduation-cap"></i>${apply.batch.name}<span style="font-size:0.8em">(${apply.batch.graduateOn?string("yyyy-MM")}毕业)</span>
     [#if apply.passed!false]审核通过
     [#else]
      <div class="btn-group">
      [@b.a href="!download?id="+apply.id target="_blank" class="btn btn-sm btn-info"]<i class="fa-solid fa-download"></i>下载申请表[/@]
      </div>
       [@b.a href="!remove?id="+apply.id onclick="return removeApply(${apply.id});" class="btn btn-sm btn-warning"]<i class="fa-solid fa-xmark"></i>删除[/@]
     [/#if]
  [/#assign]
  [@panel title=title]
    [#include "degreeApply.ftl"/]
  [/@]
  [/#if]
</div>
<script>
   function removeApply(id){
       if(confirm("确定删除?")){
          bg.form.submit(document.getElementById("removeApplyForm_"+id));
          return false;
       }else{
         return false;
       }
   }
</script>
[@b.foot/]
