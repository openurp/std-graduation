[#ftl]
[@b.head/]
  [@b.grid id="batchListTable" items=batches var="batch"]
    [@b.gridbar title="毕业批次列表"]
      bar.addItem("${b.text("action.new")}",action.add());
      bar.addItem("${b.text("action.edit")}",action.edit());
      bar.addItem("${b.text("action.delete")}",action.remove());
    [/@]
    [@b.row]
      [@b.boxcol/]
      [@b.col title="批次名称" property="name" width="20%"/]
      [@b.col title="毕业界别" property="season.name" width="30%"/]
      [@b.col title="毕业日期" property="graduateOn" width="30%"/]
      [@b.col title="是否授予学位" property="degreeOffered" width="15%"]
      ${batch.degreeOffered?string('是','否')}
      [/@]
    [/@]
  [/@]
