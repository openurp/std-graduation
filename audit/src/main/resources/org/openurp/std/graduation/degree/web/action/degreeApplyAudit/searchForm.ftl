[#ftl/]
[@b.select name="degreeApply.batch.id" items=batches label="毕业年月"/]
[@b.textfield name="degreeApply.std.code" label="学号"/]
[@b.textfield name="degreeApply.std.name" label="姓名"/]
[@b.textfield name="degreeApply.std.state.grade" label="年级"/]
[@b.select name="degreeApply.std.state.department.id" items=departs label="所在学院" empty="..."/]
[@b.textfield name="degreeApply.std.state.major.name" label="专业"/]
[@b.textfield name="degreeApply.std.state.squad.name" label="班级"/]
[@b.datepicker name="applyOn" label="申请日期"/]

