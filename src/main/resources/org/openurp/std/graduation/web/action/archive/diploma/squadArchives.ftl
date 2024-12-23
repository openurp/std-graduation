[#ftl]
[@b.head/]
    [@b.form method="post" action="!squadArchives" style="text-align: right;margin-bottom:0px"]
       <h3 width="100%" align="center" style="margin-bottom:0px">${graduateBatch.project.school.name}${graduateBatch.graduateOn?string('yyyy-MM-dd')}${archiveCategory}档案明细(${startArchiveNo})</h3>
       <input type="hidden" name="batch.id" value="${graduateBatch.id}"/>
       <label for="archiveCategory">归档内容:</label><input type="text" name="archiveCategory" id="archiveCategory" value="${archiveCategory}" style="width:120px"/>
       <label for="startArchiveNo">案卷开始号:</label><input type="text" name="startArchiveNo" id="startArchiveNo" value="${startArchiveNo}" style="width:30px"/>
       <label for="archiver">移交人:</label><input type="text" name="archiver" id="archiver" value="${archiver}" style="width:50px"/>
       <label for="archiveOn">归档时间:</label><input type="text" name="archiveOn" id="archiveOn" value="${archiveOn?string("yyyy-MM-dd")}"  style="width:90px"/>
       <input type="submit" value="提交">
    [/@]
  <div class="container-fluid">
    <table class="grid-table" width="100%" align="center">
      <thead class="grid-head">
        <tr>
          <th width="5%">案卷号及件</th>
          <th width="7%">责任者</th>
          <th width="43%">正题名</th>
          <th width="12%">起止时间</th>
          <th width="4%">页数_页码</th>
          <th width="4%">保管期限</th>
          <th width="4%">密级</th>
          <th width="7%">归档单位</th>
          <th width="6%">归档时间</th>
          <th width="5%">移交人</th>
          <th width="3%">备注</th>
        </tr>
      </thead>
      <tbody class="grid-body">
      [#assign archiveNo=startArchiveNo/]
      [#if datas?size !=0]
      [#assign totalPage=0/]
      [#list datas as data]
        [#assign thisPageSize=math.ceil(data[1]/20)/]
        [#assign totalPage=totalPage+thisPageSize/]
      [/#list]

        <tr>
          <td>${archiveNo}</td>
          <td>${datas?first[0].department.topDepartName}</td>
          <td>${graduateBatch.graduateOn?string('yyyy')}届各专业${archiveCategory}</td>
          <td>${datas?first[0].grade.code?replace('-','.')}.01-${graduateBatch.graduateOn?string('yyyy.MM.dd')}</td>
          <td>${totalPage}</td>
          <td>永久</td>
          <td>公开</td>
          <td>${datas?first[0].department.topDepartName}</td>
          <td>${archiveOn?string("yyyyMMdd")}</td>
          <td>${archiver}</td>
          <td></td>
        </tr>
      [/#if]
      [#assign pageNo=1/]
      [#list datas as data]
        <tr>
          <td>${archiveNo}.${(data_index+1)?string("0000")}</td>
          <td>${data[0].department.topDepartName}</td>
          <td>${data[0].name}${archiveCategory}</td>
          <td>${graduateBatch.graduateOn?string('yyyy.MM.dd')}</td>
          <td>[#assign thisPageSize=math.ceil(data[1]/20)/]
          [#if thisPageSize &gt; 1]${pageNo}-${thisPageSize+pageNo-1}[#else]${pageNo}[/#if]
          [#assign pageNo=pageNo+thisPageSize/]
           </td>
          <td>永久</td>
          <td>公开</td>
          <td>${data[0].department.topDepartName}</td>
          <td>${archiveOn?string("yyyyMMdd")}</td>
          <td>${archiver}</td>
          <td></td>
        </tr>
      [/#list]
      </tbody>
    </table>
  </div>
[@b.foot/]
