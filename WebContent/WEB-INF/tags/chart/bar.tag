<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ include file="/webpage/include/taglib.jsp"%>
 
<%@ attribute name="barData" type="java.util.HashMap" required="true" description="环图数据"%>

<div id="bar" style="width: 100%; height:300%;"></div>
<script src="${ctxStatic }/common/echarts.js"></script>
<script type="text/javascript">
var myChart = echarts.init(document.getElementById('bar'));
var xAxisData = [];
var data1 = [];
var data2 = [];
for (var i = 0; i < 100; i++) {
    xAxisData.push('类目' + i);
    data1.push((Math.sin(i / 5) * (i / 5 -10) + i / 6) * 5);
    data2.push((Math.cos(i / 5) * (i / 5 -10) + i / 6) * 5);
}
myChart.setOption({
    title: {
        text: '${barData.title}',
        subtext: '${barData.subTitle}'
    },
    legend: {
        data: [${barData.barName}],
        align: 'left'
    },
    toolbox: {
        // y: 'bottom',
        feature: {
            magicType: {
                type: ['stack', 'tiled']
            },
            restore: {},
            saveAsImage: {
                pixelRatio: 2
            }
        }
    },
    tooltip: {},
    xAxis: {
        data: ${barData.xAxisData},
        silent: false,
        splitLine: {
            show: false
        }
    },
    yAxis: {
    },
    series: [{
        name: 'bar1',
        type: 'bar',
        data: ${barData.bar1},
        animationDelay: function (idx) {
            return idx * 10;
        }
    }, {
        name: 'bar2',
        type: 'bar',
        data: ${barData.bar2},
        animationDelay: function (idx) {
            return idx * 10 + 100;
        }
    }],
    animationEasing: 'elasticOut',
    animationDelayUpdate: function (idx) {
        return idx * 5;
    }
});
</script>