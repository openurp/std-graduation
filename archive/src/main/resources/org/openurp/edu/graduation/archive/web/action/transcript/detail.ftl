[#ftl]
[@b.head/]
  <form name="transcriptForm" action="https://jwxt.ecupl.edu.cn/edu/eams/stdGradeReport.do?method=report" method="post">
     <input type="hidden" name="reportSetting.template" value="default"/>
     <input type="hidden" name="reportSetting.gradePrintType" value="2"/>
     <input type="hidden" name="reportSetting.pageSize" value="100"/>
     <input type="hidden" name="reportSetting.fontSize" value="11"/>
     <input type="hidden" name="reportSetting.printGp" value="1"/>
     <input type="hidden" name="reportSetting.published" value="1"/>
     <input type="hidden" name="reportSetting.order.property" value="calendar.yearTerm"/>
     <input type="hidden" name="reportSetting.order.ascending" value="1"/>
     <input type="hidden" name="reportSetting.gradeType.id" value="0"/>
     <input type="hidden" name="reportSetting.printOtherGrade" value="1"/>
     <input type="hidden" name="reportSetting.printTermGp" value="1"/>
     <input type="hidden" name="isPassOtherGrade" value="1"/>
     <input type="hidden" name="showAbsence" value="1"/>
     <input type="hidden" name="zero" value="1"/>
     <input type="hidden" name="isShowPrintAt" value="1"/>
     <input type="hidden" name="stdIds" value="[#list grs as gr]${gr.std.id}[#if gr_has_next],[/#if][/#list]"/>
     <input type="hidden" name="reportSetting.printAt" value="${graduateSession.graduateOn?string("yyyy-MM-dd")}"/>
  </form>
   <script>
        document.transcriptForm.submit();
   </script>
[@b.foot/]