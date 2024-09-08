[#ftl]
[@b.head/]
<div class="search-container">
  <div class="search-panel">
    [@b.form theme="search" name="searchForm" action="!search" title="查询条件" target="contentDiv" ]
      [@b.select name="batch.season.id" label="界别" items=seasons /]
      [@b.textfield name="batch.name" label="名称" /]
    [/@]
  </div>
  <div class="search-list">
     [@b.div id="contentDiv" /]
  </div>
</div>
<script>
  jQuery(function() {
    bg.form.submit(document.searchForm);
  });
</script>
[@b.foot/]
