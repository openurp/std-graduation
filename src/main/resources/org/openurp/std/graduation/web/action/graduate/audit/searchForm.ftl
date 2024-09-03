[#ftl/]
<tbody>
  <input type="hidden" name="audited" value="1" />
  [@b.select label="批次" name="result.batch.id" items=batches value=batches?first required="true"/]
  [@b.textfield name="result.std.code" label="学号" maxlength="50000"/]
  [@b.textfield name="result.std.name" label="姓名" /]
  [@b.textfield name="result.std.state.grade.code" label="年级" /]
  [@b.select name="result.std.level.id" items=levels label="培养层次" empty="..."/]
  [@b.select name="result.std.state.department.id" items=departs label="院系" empty="..."/]
  [@b.select name="result.std.stdType.id" items=stdTypes label="学生类别" empty="..."/]
  [@b.textfield name="result.std.state.major.name" label="专业名称"/]
  [@b.textfield name="result.std.state.squad.name" label="班级名称" /]
  [@b.select name="result.passed" label="是否通过"]
    <option value="">...</option>
    <option value="1">是</option>
    <option value="0">否</option>
    <option value="null">未审核</option>
  [/@]
</tbody>
