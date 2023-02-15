[#ftl]
[@b.head/]

<div class="container" style="width:95%">

<nav class="navbar navbar-default" role="navigation">
  <div class="container-fluid">
    <div class="navbar-header">
        <a class="navbar-brand" href="#"><span class="glyphicon glyphicon-book"></span>学位申请</a>
    </div>
    [#if degreeResult??]
    <ul class="nav navbar-nav navbar-right">
        <li>
        [@b.form class="navbar-form navbar-left" role="search" action="!doApply?batch.id="+degreeResult.batch.id]
            [@b.a class="btn btn-sm btn-info" href="!doApply"]<span class='glyphicon glyphicon-plus'></span>申请[/@]
        [/@]
        </li>
    </ul>
    [/#if]
    </div>
</nav>

<div class="jumbotron">
    <div class="container">
        <h2>学位申请</h2>
        [#if degreeResult?? && program?? && program.degree??]
        <p>你还没有申请学位，找到你的毕业审核通过记录，现在就申请${program.degree.name}学位。</p>
        <p>
         [@b.a class="btn btn-lg btn-info" role="button" href="!doApply?batch.id="+degreeResult.batch.id]<span class='glyphicon glyphicon-plus'></span>申请 ${program.degree.name}学位[/@]
        </p>
        [#elseif !(program?? && program.degree??)]
           <p>没有找到你所在专业对应的学位类型，还不能申请。</p>
        [#else]
           <p>没有找到你毕业审核的通过记录，现在还不能申请学位。</p>
        [/#if]
    </div>
</div>
[@b.foot/]
