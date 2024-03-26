[#ftl/]
[@b.head/]
[@b.grid items=results! var="result" id="result_list_table"]
  [@b.gridbar]
    bar.addItem('审核', action.multi("audit"));
    bar.addItem('未过课程', action.multi("failCourses"));
  [/@]
  [@b.row]
  [@b.boxcol/]
  [@b.col title="学号" property="std.code" width="10%"/]
  [@b.col title="姓名" property="std.name" width="7%"]
    <div class="text-ellipsis">${(result.std.name)!}</div>
  [/@]
  [@b.col title="年级" property="std.state.grade" width="5%" /]
  [@b.col title="培养层次" property="std.level.name" width="6%" /]
  [@b.col title="院系" property="std.state.department.name" width="6%"]
    ${result.std.state.department.shortName!result.std.state.department.name}
  [/@]
  [@b.col title="专业" property="std.state.major.name" width="8%" ]
    <div class="text-ellipsis">${(result.std.state.major.name)!}</div>
  [/@]
  [@b.col title="应修" property="auditStat.requiredCredits" width="5%"/]
  [@b.col title="缺口" property="neededCredits" width="5%"] [#if result.neededCredits>0]${result.neededCredits}[/#if] [/@]
  [@b.col title="在读" property="auditStat.takingCredits" width="5%"][#if result.auditStat.takingCredits>0]${result.auditStat.takingCredits+result.auditStat.predictedCredits}[/#if][/@]
  [@b.col title="预计缺" property="neededCredits3" width="5%"] [#if result.neededCredits3>0]${result.neededCredits3}[/#if] [/@]
  [@b.col title="结果" property="passed" width="10%"]
    [@b.a href="!info?id=" + result.id title="查看审核结果" target="_blank"]${result.passed?string("完成","<font color='red'>未完成</font>")}[/@]
    [#if !result.passed && result.predicted]<span class="text-muted">预计&#10004;</span>[/#if]
  [/@]
  [@b.col title="未完成学分"]
    <div class="text-ellipsis">
    [#list result.groupResults?sort_by("indexno") as r]
      [#if (!r.parent?? || !(r.parent.parent??)) && !r.passed && !r.predicted && r.auditStat.neededCredits>0]${r.courseType.name}${r.auditStat.neededCredits}分&nbsp;[/#if]
    [/#list]
    </div>
  [/@]
  [/@]
[/@]
<script>
  jQuery(function() {
    $('#result_list_table [data-toggle="tooltip"]').tooltip({'html':true})
  });
</script>
[@b.foot/]
