[#ftl]
[@b.head/]
  <form name="transcriptForm" action="/edu/grade/transcript/final!report.action" method="post">
     <input type="hidden" name="template" value="default"/>
     <input type="hidden" name="orderBy" value="std.state.department.code,std.state.squad.code,std.code"/>
     <input type="hidden" name="stdIds" value="[#list gs as g]${g.std.id}[#if g_has_next],[/#if][/#list]"/>
  </form>
   <script>
        document.transcriptForm.submit();
   </script>
[@b.foot/]
