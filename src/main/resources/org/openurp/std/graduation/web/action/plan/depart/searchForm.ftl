[#ftl/]
<tbody>
  <input type="hidden" name="audited" value="1" />
  [@b.select label="批次" name="batch.id" items=batches value=batches?first.id /]
  [@b.textfield name="result.std.code" label="学号" /]
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
  [/@]
  [@b.select name="predicted" label="预计情况"]
    <option value="">...</option>
    <option value="passed">预计通过</option>
    <option value="unpassed">预计不通过</option>
    <option value="takingPassed">在读及格后通过</option>
    <option value="takingFailed">在读及格后仍不通过</option>
  [/@]
  [@b.field label="应修学分"]
    <input type="text" value="" name="requiredCredits.from" style="width:43px"/>-<input type="text" value="" name="requiredCredits.to" style="width:43px"/>
  [/@]
  [@b.field label="实修学分"]
    <input type="text" value="" name="passedCredits.from" style="width:43px"/>-<input type="text" value="" name="passedCredits.to" style="width:43px"/>
  [/@]
</tbody>
