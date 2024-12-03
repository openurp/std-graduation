[#ftl]
[@b.head/]
  [@b.toolbar title="未通过学生成绩统计"/]
<div class="search-container">
  <div class="search-panel">
        [@b.form title="ui.searchForm" name="searchForm" action="!search" target="unpasses" theme="search"]
          <input type="hidden" name="orderBy" value="result.groupResult.planResult.std.state.grade desc,result.id">
          [@b.select name="batch.id" items=batches label="毕业年月" value=batches?first/]
          [@b.textfield name="result.groupResult.planResult.std.code" label="学号" /]
          [@b.textfield name="result.groupResult.planResult.std.name" label="姓名" /]
          [@b.textfield name="result.groupResult.planResult.std.state.grade.code" label="年级" /]
          [@b.select name="result.groupResult.planResult.std.state.department.id" items=departs label="院系" empty="..."/]
          [@b.textfield name="result.groupResult.planResult.std.state.major.name" label="专业名称" /]
          [@b.textfield name="result.groupResult.planResult.std.state.squad.name" label="班级名称" /]
          [@b.textfield name="result.course.name" label="课程名称" /]
          [@b.select label="修读情况" name="result.hasGrade" items={'1':'修读过','0':'未修读过'} empty="..."/]
          [@b.select label="预计通过" name="result.predicted" items={'1':'是','0':'否'} empty="..."/]
          [@b.select label="是否在读" name="result.taking" items={'1':'是','0':'否'} empty="..."/]
          [@b.select label="学籍有效" name="stdActive" items={'1':'有效','0':'无效'} value="1"/]
        [/@]
  </div>
  <div class="search-list">
      [@b.div id="unpasses"/]
  </div>
</div>
  <script>
    $(function() {
      $(document).ready(function() {
        bg.form.submit(document.searchForm, "${b.url("!search")}", "unpasses");
      });
    });
  </script>
[@b.foot/]
