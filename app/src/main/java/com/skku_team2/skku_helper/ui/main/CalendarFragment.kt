package com.skku_team2.skku_helper.ui.main

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import com.skku_team2.skku_helper.R
import com.skku_team2.skku_helper.databinding.FragmentCalendarBinding
import com.skku_team2.skku_helper.utils.getColorAttr
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.Calendar
import java.util.Locale


class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: CalendarViewModel by viewModels()

    private lateinit var calendarAssignmentAdapter: AssignmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarAssignmentAdapter = AssignmentAdapter(
            token = mainViewModel.token,
            emptyItemMessage = requireContext().getString(R.string.main_calendar_empty_item_message),
            onLongClick = { assignmentData ->
                CoroutineScope(Dispatchers.Main).launch {
                    MaterialAlertDialogBuilder(requireContext()).apply {
                        setTitle(R.string.main_dialog_delete_title)
                        setMessage(R.string.main_dialog_delete_message)
                        setNegativeButton(R.string.main_dialog_delete_cancel, null)
                        setPositiveButton(R.string.main_dialog_delete_confirm) { _, _ ->
                            lifecycleScope.launch {
                                mainViewModel.deleteAssignment(assignmentData.course.id, assignmentData.assignment.id)
                                mainViewModel.fetch()
                            }
                        }
                        create().show()
                    }
                }
                true
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = calendarAssignmentAdapter

        binding.calendarView.isDynamicHeightEnabled = true
        binding.calendarView.selectedDate = LocalDate.now().run { CalendarDay.from(year, monthValue - 1, dayOfMonth) }
        binding.calendarView.state().edit()
            .setMinimumDate(CalendarDay.from(Calendar.getInstance().apply { add(Calendar.MONTH, -6) }))
            .setMaximumDate(CalendarDay.from(Calendar.getInstance().apply { add(Calendar.MONTH, +6) }))
            .commit()
        binding.calendarView.setTitleFormatter { calendarDay ->
            SimpleDateFormat("yyyy/MM", Locale.getDefault()).format(calendarDay.date)
        }
        binding.calendarView.setWeekDayFormatter { dayOfWeek ->
            when (dayOfWeek) {
                1 -> requireContext().getString(R.string.main_calendar_sun)
                2 -> requireContext().getString(R.string.main_calendar_mon)
                3 -> requireContext().getString(R.string.main_calendar_tue)
                4 -> requireContext().getString(R.string.main_calendar_wed)
                5 -> requireContext().getString(R.string.main_calendar_thu)
                6 -> requireContext().getString(R.string.main_calendar_fri)
                7 -> requireContext().getString(R.string.main_calendar_sat)
                else -> requireContext().getString(R.string.main_calendar_undefined)
            }
        }
        binding.calendarView.setOnMonthChangedListener { widget, date ->
            val yearMonth = YearMonth.of(date.year, date.month + 1)
            val weekFields = WeekFields.of(Locale.getDefault())
            val weeksInMonth = yearMonth.atEndOfMonth().get(weekFields.weekOfMonth())

            val tileHeightPx = 48.dpToPx()
            val headerHeightPx = 96.dpToPx()
            val paddingVerticalPx = binding.calendarView.paddingTop + binding.calendarView.paddingBottom

            val startHeight = widget.height
            val targetHeight = headerHeightPx + (weeksInMonth * tileHeightPx) + paddingVerticalPx

            if (startHeight != targetHeight) {
                TransitionManager.beginDelayedTransition(
                    binding.root as ViewGroup,
                    AutoTransition().apply { duration = 200 }
                )
                widget.updateLayoutParams {
                    height = targetHeight
                }
            }
        }
        binding.calendarView.setOnDateChangedListener { _, date, _ ->
            val selectedDate = LocalDate.of(date.year, date.month + 1, date.day)
            viewModel.selectDate(
                selectedDate = selectedDate,
                assignmentDataList = mainViewModel.assignmentDataListState.value
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    mainViewModel.assignmentDataListState.collect { assignmentDataList ->
                        viewModel.selectDate(
                            selectedDate = viewModel.uiState.value.selectedDate,
                            assignmentDataList = assignmentDataList
                        )

                        if (assignmentDataList == null) {
                            binding.calendarView.removeDecorators()
                            binding.calendarView.invalidateDecorators()
                        } else {
                            val calendarDayList = viewModel.getDateList(assignmentDataList)
                                .map { localDate ->
                                    localDate.run { CalendarDay.from(year, monthValue - 1, dayOfMonth) }
                                }

                            binding.calendarView.removeDecorators()
                            binding.calendarView.invalidateDecorators()
                            binding.calendarView.addDecorator(
                                object : DayViewDecorator {
                                    override fun shouldDecorate(day: CalendarDay?) = calendarDayList.contains(day)
                                    override fun decorate(view: DayViewFacade?) {
                                        val backgroundDrawable = requireContext().getDrawable(R.drawable.gradient_start)
                                        if (backgroundDrawable != null && view != null) {
                                            view.addSpan(
                                                DotSpan(
                                                    6F,
                                                    requireContext().getColorAttr(com.google.android.material.R.attr.colorPrimaryContainer)
                                                )
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
                launch {
                    viewModel.uiState.collect { uiState ->
                        calendarAssignmentAdapter.submitList(uiState.selectedDateAssignmentDataList)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}
