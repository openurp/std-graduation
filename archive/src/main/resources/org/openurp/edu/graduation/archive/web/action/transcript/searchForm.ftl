[#ftl/]
[@b.select name="session.id" items=sessions label="毕业批次"/]
[@b.select name="passed" label="是否通过" items={"":"全部","1":"通过","0":"不通过"} value=""/]
[@b.textfield name="batch" label="批次" /]