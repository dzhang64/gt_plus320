<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ include file="/webpage/include/taglib.jsp"%>

<%@ attribute name="lineData" type="java.util.HashMap" required="true" description="线图数据"%>

<div id="line" style="width: 100%; height:300%;"></div>
<script src="${ctxStatic }/common/echarts.js"></script>
<script type="text/javascript">
var myChart = echarts.init(document.getElementById('line'));
myChart.setOption({
    title: {
        text: '${lineData.title}',
        subtext: '${lineData.subTitle}',
        left: 'center'
    },
    tooltip: {
        trigger: 'axis'
    },
    legend: {
        data:['温度','湿度'],
        left: 'left'
    },
    toolbox: {
        show: true,
        feature: {
            magicType: {type: ['line', 'bar']},
            restore: {},
            saveAsImage: {}
        }
    },
    xAxis:  {
        type: 'category',
        boundaryGap: false,
        data: ${lineData.date}
    },
    yAxis: {
        type: 'value',
        axisLabel: {
            formatter: '{value}'
        }
    },
    series: [
        {
            name:'温度',
            type:'line',
            data:${lineData.temperature}
        },
        {
            name:'湿度',
            type:'line',
            data:${lineData.humidity}
        }
    ]
});
</script>