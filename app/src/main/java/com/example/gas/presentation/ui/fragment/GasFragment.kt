package com.example.gas.presentation.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.gas.R
import com.example.gas.presentation.viewmodel.GasViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import java.time.ZoneId

@AndroidEntryPoint
class GasFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var lineChart: LineChart
    private lateinit var tvTitle: TextView
    private lateinit var tvHeartRateValue: TextView
    private lateinit var tvBpmLabel: TextView
    private lateinit var tvMinValue: TextView
    private lateinit var tvMaxValue: TextView
    private lateinit var tvAverageLabel: TextView
    private lateinit var ivHeartIcon: ImageView

    private val heartRateData = mutableListOf<Float>()
    private val timeStamps = mutableListOf<Long>()
    private lateinit var timeFrame: String

    private val maxDataPoints = 100

    private val viewModel: GasViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gas, container, false)

        tabLayout = view.findViewById(R.id.tab_layout)
        lineChart = view.findViewById(R.id.chart_heart_rate)
        tvTitle = view.findViewById(R.id.tv_title)
        tvHeartRateValue = view.findViewById(R.id.tv_heart_rate_value)
        tvBpmLabel = view.findViewById(R.id.tv_bpm_label)
        tvMinValue = view.findViewById(R.id.tv_min_value)
        tvMaxValue = view.findViewById(R.id.tv_max_value)
        tvAverageLabel = view.findViewById(R.id.tv_average_label)
        ivHeartIcon = view.findViewById(R.id.iv_heart_icon)

        setupTabLayout()
        setupLineChart()

        timeFrame = "MINUTE"

        observeHeartRate()

        return view
    }

    private fun observeHeartRate() {
        viewModel.heartRateHistory.observe(viewLifecycleOwner) { measurements ->
            if (measurements.isNullOrEmpty()) return@observe

            val lastTimestamp = if (timeStamps.isEmpty()) 0L else timeStamps.last()

            for (m in measurements) {
                val epochMillis = m.dateTime
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

                if (epochMillis > lastTimestamp) {
                    if (heartRateData.size >= maxDataPoints) {
                        heartRateData.removeAt(0)
                        timeStamps.removeAt(0)
                    }
                    heartRateData.add(m.gas)
                    timeStamps.add(epochMillis)
                }
            }
            updateChartData()
        }
    }

    private fun setupTabLayout() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                timeFrame = when (tab?.position) {
                    0 -> "MINUTE"
                    1 -> "HOUR"
                    2 -> "DAY"
                    3 -> "WEEK"
                    else -> "MINUTE"
                }
                updateChartData()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupLineChart() {
        lineChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            legend.isEnabled = false
            xAxis.apply {
                setDrawGridLines(false)
                position = XAxis.XAxisPosition.BOTTOM
                textColor = Color.BLACK
                textSize = 10f
                labelRotationAngle = 45f
            }
            axisLeft.apply {
                setDrawGridLines(true)
                axisMinimum = 0f
                axisMaximum = 1000f
            }
            axisRight.isEnabled = false
            setBackgroundColor(Color.TRANSPARENT)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateChartData() {
        val displayData = if (heartRateData.size > 20) {
            heartRateData.takeLast(20)
        } else {
            heartRateData
        }
        val displayTimestamps = if (timeStamps.size > 20) {
            timeStamps.takeLast(20)
        } else {
            timeStamps
        }

        val entries = heartRateData.mapIndexed { index, value ->
            Entry(index.toFloat(), value)
        }

        val labels = timeStamps.map {
            val label = when (timeFrame) {
                "MINUTE" -> "${(it / 1000) % 60}s"
                "HOUR" -> "${(it / 1000 / 60) % 60}m"
                "DAY" -> "${(it / 1000 / 3600) % 24}h"
                "WEEK" -> {
                    when ((it / 1000 / 86400).toInt() % 7) {
                        0 -> "Mon"
                        1 -> "Tue"
                        2 -> "Wed"
                        3 -> "Thu"
                        4 -> "Fri"
                        5 -> "Sat"
                        else -> "Sun"
                    }
                }
                else -> "${(it / 1000) % 60}s"
            }
            label
        }

        val minValue = heartRateData.minOrNull() ?: 0f
        val maxValue = heartRateData.maxOrNull() ?: 0f
        val averageValue = heartRateData.average().toFloat()
        val latestBpm = heartRateData.lastOrNull()?.toInt() ?: 0

        tvHeartRateValue.text = "$latestBpm"
        tvMinValue.text = "${minValue.toInt()}"
        tvMaxValue.text = "${maxValue.toInt()}"
        tvAverageLabel.text = "Average ${averageValue.toInt()} PPM"

        val maxAlertThreshold = 120f
        val minAlertThreshold = 55f
        val isAlert = heartRateData.any { it > maxAlertThreshold || it < minAlertThreshold}

        val gradientColors = if (isAlert) {
            intArrayOf(
                resources.getColor(R.color.chart_gradient_alert_top, null),
                resources.getColor(R.color.chart_gradient_alert_bottom, null)
            )
        } else {
            intArrayOf(
                resources.getColor(R.color.chart_gradient_normal_top, null),
                resources.getColor(R.color.chart_gradient_normal_bottom, null)
            )
        }

        val textColor = if (isAlert) R.color.alert_text_color else R.color.primary_text_color
        listOf(
            tvTitle, tvHeartRateValue,
            tvBpmLabel, tvMinValue, tvMaxValue, tvAverageLabel
        ).forEach {
            it.setTextColor(ContextCompat.getColor(requireContext(), textColor))
        }
        ivHeartIcon.setColorFilter(ContextCompat.getColor(requireContext(), textColor))

        val chartValueColor = if (isAlert) R.color.alert_text_color else R.color.chart_value_text_normal

        val dataSet = LineDataSet(entries, "Gas (PPM)").apply {
            mode = LineDataSet.Mode.CUBIC_BEZIER
            cubicIntensity = 0.2f
            color = resources.getColor(R.color.chart_line_color, null)
            setDrawCircles(false)
//            setCircleColor(Color.BLACK)
            lineWidth = 3.0f // line width
            circleRadius = 5f
            setDrawCircleHole(false)
            setDrawValues(false) // erase value in graph
            valueTextColor = resources.getColor(chartValueColor, null)
            valueTextSize = 10f
            setDrawFilled(true)
            fillDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, gradientColors)
        }

        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        lineChart.data = LineData(dataSet)
        lineChart.invalidate()

        lineChart.axisLeft.axisMinimum = (displayData.minOrNull() ?: 0f) - 10f
        lineChart.axisLeft.axisMaximum = (displayData.maxOrNull() ?: 100f) + 10f

    }
}