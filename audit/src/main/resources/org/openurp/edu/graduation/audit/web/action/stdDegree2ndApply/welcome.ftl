[#ftl]
[@b.head/]

<div class="container" style="width:95%">

<nav class="navbar navbar-default" role="navigation">
  <div class="container-fluid">
    <div class="navbar-header">
        <a class="navbar-brand" href="#"><span class="glyphicon glyphicon-book"></span>法学专业第二学士学位报名查询公共基础课总绩点</a>
    </div>
    [#if graduateResult??]
    <ul class="nav navbar-nav navbar-right">
        <li>
        [@b.form class="navbar-form navbar-left" role="search" action="!apply?session.id="+graduateResult.session.id]
            [@b.a class="btn btn-sm btn-info" href="!apply"]<span class='glyphicon glyphicon-plus'></span>申请查询[/@]
        [/@]
        </li>
    </ul>
    [/#if]
    </div>
</nav>

<div class="jumbotron">
    <div class="container">
        <h2>申请查询公共课基础绩点查询</h2>
        [#if graduateResult??]
        <p>你还没有申请查询公共基础课总绩点，现在点击申请。</p>
        <p>
         [@b.a class="btn btn-lg btn-info" role="button" href="!apply?session.id="+graduateResult.session.id]<span class='glyphicon glyphicon-plus'></span>申请查询公共基础课总绩点[/@]
        </p>
        [#else]
           <p>没有找到你毕业审核的记录，现在还不能申请。</p>
        [/#if]
    </div>
</div>

[@b.foot/]