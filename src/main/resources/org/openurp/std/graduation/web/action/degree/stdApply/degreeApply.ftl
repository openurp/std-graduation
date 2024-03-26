[#assign std=apply.std]
<table class="infoTable">
   <tr>
     <td class="title">学号</td><td>${std.code}</td>
     <td class="title">姓名</td><td>${std.name}</td>
     <td class="title">性别</td><td>${(std.person.gender.name)!}</td>
   </tr>
   <tr>
      <td class="title">所在学院</td><td>${std.state.department.name}</td>
      <td class="title">专业</td><td>${(std.state.major.name)!} ${(std.state.direction.name)!}</td>
      <td class="title">班级</td><td>${(std.state.squad.name)!}</td>
   </tr>
   <tr>
      <td class="title">学制</td><td>${std.duration}年</td>
      <td class="title">入学时间</td><td>${(std.studyOn?string("yyyy年MM月dd日"))!}</td>
      <td class="title">毕业时间</td><td>${apply.batch.graduateOn?string("yyyy年MM月dd日")}</td>
   </tr>
   <tr>
      <td class="title">申请何种学位</td><td>${apply.degree.name}</td>
      <td class="title">申请学位时间</td><td>${apply.updatedAt?string("yyyy年MM月dd日")}</td>
      <td colspan="2"></td>
   </tr>
</table>
