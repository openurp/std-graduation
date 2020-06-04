[#ftl]
[@b.head/]

<div class="container" style="width:95%">

<nav class="navbar navbar-default" role="navigation">
  <div class="container-fluid">
    <div class="navbar-header">
        <a class="navbar-brand" href="#"><span class="glyphicon glyphicon-book"></span>学位申请</a>
    </div>
    [#if graduationResult??]
    <ul class="nav navbar-nav navbar-right">
        <li>
        [@b.form class="navbar-form navbar-left" role="search" action="!apply?session.id="+graduationResult.id]
            [@b.a class="btn btn-sm btn-info" href="!apply"]<span class='glyphicon glyphicon-plus'></span>申请[/@]
        [/@]
        </li>
    </ul>
    [/#if]
    </div>
</nav>

<div class="jumbotron">
    <div class="container">
        <h2>学位申请</h2>
        [#if graduationResult?? && program?? && program.degree??]
        <p>你还没有申请学位，找到你的毕业审核通过记录，现在就申请${program.degree.name}学位。</p>
        <p>
         [@b.a class="btn btn-lg btn-info" role="button" href="!apply?session.id="+graduationResult.session.id]<span class='glyphicon glyphicon-plus'></span>申请 ${program.degree.name}学位[/@]
        </p>
        [#else]
           <p>没有找到你毕业审核的通过记录，现在还不能申请学位。</p>
        [/#if]
    </div>
</div>
</div>
[@b.foot/]