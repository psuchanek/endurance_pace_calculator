package dev.psuchanek.endurancepacecalculator.ui.pace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.slider.Slider
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import dev.psuchanek.endurancepacecalculator.R
import dev.psuchanek.endurancepacecalculator.databinding.LayoutPaceCalculatorBinding
import dev.psuchanek.endurancepacecalculator.utils.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PaceCalculatorFragment : Fragment(R.layout.layout_pace_calculator) {

    private lateinit var binding: LayoutPaceCalculatorBinding
    private val paceViewModel: PaceViewModel by viewModels()


    private lateinit var tvRunDurationHours: MaterialTextView
    private lateinit var tvRunDurationMinutes: MaterialTextView
    private lateinit var tvRunDurationSeconds: MaterialTextView

    private lateinit var tvTriathlonDurationHours: MaterialTextView
    private lateinit var tvTriathlonDurationMinutes: MaterialTextView
    private lateinit var tvTriathlonDurationSeconds: MaterialTextView

    private lateinit var sliderSwim: Slider
    private lateinit var sliderTransitionOne: Slider
    private lateinit var sliderBike: Slider
    private lateinit var sliderTransitionTwo: Slider
    private lateinit var sliderRun: Slider
    private lateinit var sliderRunPace: Slider


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutPaceCalculatorBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        setupSlidersTouchListeners()
        setupObservers()
        initAdapters()
        setupSpinnerListeners()
        submitPaceValuesToViewModel(sliderRunPace.value)
    }


    private fun initUI() {
        binding.layoutRunning.layoutRunningTime.apply {
            this@PaceCalculatorFragment.tvRunDurationHours = tvDurationHours
            this@PaceCalculatorFragment.tvRunDurationMinutes = tvDurationMinutes
            this@PaceCalculatorFragment.tvRunDurationSeconds = tvDurationSeconds
        }
        sliderRunPace = binding.layoutRunning.sliderRunPace

        binding.layoutTriathlon.apply {
            sliderSwim = swimSlider
            sliderTransitionOne = transitionOneSlider
            sliderBike = bikeSlider
            sliderTransitionTwo = transitionTwoSlider
            sliderRun = runSlider

            layoutTriathlonTotalDurationTime.apply {
                tvTriathlonDurationHours = tvDurationHours
                tvTriathlonDurationMinutes = tvDurationMinutes
                tvTriathlonDurationSeconds = tvDurationSeconds
            }
        }
    }

    private fun setupSlidersTouchListeners() {
        sliderSwim.addOnChangeListener(addSliderOnValueChangeListener())
        sliderTransitionOne.addOnChangeListener(addSliderOnValueChangeListener())
        sliderBike.addOnChangeListener(addSliderOnValueChangeListener())
        sliderTransitionTwo.addOnChangeListener(addSliderOnValueChangeListener())
        sliderRun.addOnChangeListener(addSliderOnValueChangeListener())
        sliderRunPace.addOnChangeListener(addSliderOnValueChangeListener())
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            paceViewModel.runDurationValues.collect { durationList ->
                updateRunDurationUI(durationList)
            }
        }

        lifecycleScope.launch {
            paceViewModel.runPaceValues.collect { paceList ->
                updatePaceUI(paceList)
            }
        }

        lifecycleScope.launch {
            paceViewModel.triSwimPaceValue.collect {
                binding.layoutTriathlon.tvSwimPace.text = it
            }
        }

        lifecycleScope.launch {
            paceViewModel.transitionOneValue.collect {
                binding.layoutTriathlon.tvTransitionOneTime.text = it
            }
        }

        lifecycleScope.launch {
            paceViewModel.triBikePaceValue.collect {
                binding.layoutTriathlon.tvBikePace.text = it
            }
        }

        lifecycleScope.launch {
            paceViewModel.transitionTwoValue.collect {
                binding.layoutTriathlon.tvTransitionTwoTime.text = it
            }

        }

        lifecycleScope.launch {
            paceViewModel.triRunPaceValue.collect {
                binding.layoutTriathlon.tvRunPace.text = it
            }
        }

        lifecycleScope.launch {
            paceViewModel.triDurationValue.collect { durationList ->
                updateTriathlonDurationUI(durationList)
            }
        }

        lifecycleScope.launch {
            paceViewModel.activitiesTotalTimes.collect { activitiesTotalTimes ->
                updateTotalTimesUI(activitiesTotalTimes)

            }
        }
    }


    private fun updatePaceUI(paceList: List<String>) {
        binding.layoutRunning.layoutRunningPace.apply {
            tvRunPaceMinutes.text = paceList[0]
            tvRunPaceSeconds.text = paceList[1]
        }

    }

    private fun updateRunDurationUI(durationList: List<String>) {
        tvRunDurationHours.text = durationList[0]
        tvRunDurationMinutes.text = durationList[1]
        tvRunDurationSeconds.text = durationList[2]


    }

    private fun updateTriathlonDurationUI(durationList: List<String>) {
        tvTriathlonDurationHours.text = durationList[0]
        tvTriathlonDurationMinutes.text = durationList[1]
        tvTriathlonDurationSeconds.text = durationList[2]


    }

    private fun updateTotalTimesUI(activitiesTotalTimes: List<String>) {
        if (activitiesTotalTimes.isNotEmpty()) {
            binding.layoutTriathlon.tvSwimTotalTime.text = activitiesTotalTimes[0]
            binding.layoutTriathlon.tvBikeTotalTime.text = activitiesTotalTimes[1]
            binding.layoutTriathlon.tvRunTotalTime.text = activitiesTotalTimes[2]
        }
    }

    private fun initAdapters() {
        val activityAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.activities_for_pace_calculator)
        )
        binding.dropDownPaceChoiceSpinner.apply {
            setAdapter(activityAdapter)
            setText(ACTIVITY_PRESET_VALUE, false)
        }
        binding.tiLayoutDropDownPaceChoice.hint = resources.getString(R.string.activities_label)

    }

    private fun addSliderOnValueChangeListener() =
        Slider.OnChangeListener { slider, value, _ ->
            when (slider.id) {
                sliderSwim.id -> {
                    submitTriathlonStagePaceToViewModel(value, TriStage.SWIM)
                }
                sliderTransitionOne.id -> {
                    submitTriathlonStagePaceToViewModel(value, TriStage.T1)
                }
                sliderBike.id -> {
                    submitTriathlonStagePaceToViewModel(value, TriStage.BIKE)
                }
                sliderTransitionTwo.id -> {
                    submitTriathlonStagePaceToViewModel(value, TriStage.T2)
                }
                sliderRun.id -> {
                    submitTriathlonStagePaceToViewModel(value, TriStage.RUN)
                }
                sliderRunPace.id -> {
                    submitPaceValuesToViewModel(value)
                }
            }
        }

    private fun submitPaceValuesToViewModel(value: Float) {
        paceViewModel.submitRunPaceValue(value)
    }

    private fun submitTriathlonStagePaceToViewModel(value: Float, stage: TriStage) {
        paceViewModel.submitTriathlonStagePace(value, stage)
    }

    private fun setupSpinnerListeners() {
        binding.dropDownPaceChoiceSpinner.onItemClickListener = activitySpinnerOnItemClickListener()
    }

    private fun activitySpinnerOnItemClickListener() =
        AdapterView.OnItemClickListener { _, _, position, _ ->
            when (position) {
                ACTIVITY_CHOICE_RUN_5KM, ACTIVITY_CHOICE_RUN_10KM, ACTIVITY_CHOICE_RUN_HALF_MARATHON, ACTIVITY_CHOICE_RUN_FULL_MARATHON -> {
                    switchRunAndTriathlonCalculatorVisibility()
                    position.setActivityTypeFromPosition()
                }

                ACTIVITY_CHOICE_TRIATHLON_SPRINT, ACTIVITY_CHOICE_TRIATHLON_OLYMPIC, ACTIVITY_CHOICE_TRIATHLON_HALF, ACTIVITY_CHOICE_TRIATHLON_FULL -> {
                    switchRunAndTriathlonCalculatorVisibility(false)
                    position.setActivityTypeFromPosition()
                }
            }
        }

    private fun switchRunAndTriathlonCalculatorVisibility(visibility: Boolean = true) {
        binding.layoutRunning.root.isVisible = visibility
        binding.layoutTriathlon.root.isVisible = !visibility
    }

    private fun Int.setActivityTypeFromPosition() {
        when (this) {
            ACTIVITY_CHOICE_RUN_5KM -> {
                ActivityType.RUN_FIVE_KM.setActivityTypeInViewModel()
            }
            ACTIVITY_CHOICE_RUN_10KM -> {
                ActivityType.RUN_TEN_KM.setActivityTypeInViewModel()
            }
            ACTIVITY_CHOICE_RUN_HALF_MARATHON -> {
                ActivityType.RUN_HALF_MARATHON.setActivityTypeInViewModel()
            }
            ACTIVITY_CHOICE_RUN_FULL_MARATHON -> {
                ActivityType.RUN_FULL_MARATHON.setActivityTypeInViewModel()
            }
            ACTIVITY_CHOICE_TRIATHLON_SPRINT -> {
                ActivityType.SPRINT.setActivityTypeInViewModel()
            }
            ACTIVITY_CHOICE_TRIATHLON_OLYMPIC -> {
                ActivityType.OLYMPIC.setActivityTypeInViewModel()
            }
            ACTIVITY_CHOICE_TRIATHLON_HALF -> {
                ActivityType.HALF_TRIATHLON.setActivityTypeInViewModel()
            }
            ACTIVITY_CHOICE_TRIATHLON_FULL -> {
                ActivityType.FULL_TRIATHLON.setActivityTypeInViewModel()
            }
        }
        when (this) {
            in ACTIVITY_CHOICE_RUN_5KM..ACTIVITY_CHOICE_RUN_FULL_MARATHON -> {
                submitPaceValuesToViewModel(sliderRunPace.value)
            }
            in ACTIVITY_CHOICE_TRIATHLON_SPRINT..ACTIVITY_CHOICE_TRIATHLON_FULL -> {
                submitTriathlonStagePaceToViewModel(sliderSwim.value, TriStage.SWIM)
                submitTriathlonStagePaceToViewModel(sliderTransitionOne.value, TriStage.T1)
                submitTriathlonStagePaceToViewModel(sliderBike.value, TriStage.BIKE)
                submitTriathlonStagePaceToViewModel(sliderTransitionTwo.value, TriStage.T2)
                submitTriathlonStagePaceToViewModel(sliderRun.value, TriStage.RUN)
            }
        }

    }

    private fun ActivityType.setActivityTypeInViewModel() {
        paceViewModel.setActivityType(this)
    }

    companion object {
        private const val ACTIVITY_CHOICE_RUN_5KM = 0
        private const val ACTIVITY_CHOICE_RUN_10KM = 1
        private const val ACTIVITY_CHOICE_RUN_HALF_MARATHON = 2
        private const val ACTIVITY_CHOICE_RUN_FULL_MARATHON = 3

        private const val ACTIVITY_CHOICE_TRIATHLON_SPRINT = 4
        private const val ACTIVITY_CHOICE_TRIATHLON_OLYMPIC = 5
        private const val ACTIVITY_CHOICE_TRIATHLON_HALF = 6
        private const val ACTIVITY_CHOICE_TRIATHLON_FULL = 7
    }
}


