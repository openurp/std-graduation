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
        <a class="navbar-brand" href="#"><span class="glyphicon glyphicon-book"></span>法学专业第二学士学位报名查询公共基础课总绩点</a>
    </div>
  </div>
</nav>
  [#if degree2ndApply??]
  [@b.form name="removeApplyForm_"+degree2ndApply.id  action="!remove?id="+degree2ndApply.id+"&_method=delete"][/@]
  [#assign title]
     <span class="glyphicon glyphicon-bookmark"></span>${degree2ndApply.session.name}<span style="font-size:0.8em">(${degree2ndApply.session.graduateOn?string("yyyy-MM")}毕业)</span>
       [@b.a href="!remove?id="+degree2ndApply.id onclick="return removeApply(${degree2ndApply.id});" class="btn btn-sm btn-warning"]<span class="glyphicon glyphicon-remove"></span>删除[/@]
  [/#assign]
  [@panel title=title]
    [#include "degree2ndApply.ftl"/]
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