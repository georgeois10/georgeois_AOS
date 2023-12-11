import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class DateTimePicker(private val context: Context) {

    interface DateTimeListener {
        fun onDateTimeSelected(dateTime: String)
    }

    private var dateTimeListener: DateTimeListener? = null

    fun setDateTimeListener(listener: DateTimeListener) {
        dateTimeListener = listener
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showDateTimePicker() {
        // 호출할 때 사용할 날짜 선택 이벤트 핸들러
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                // 여기서 선택된 날짜를 처리할 수 있습니다.
                val selectedDate = "${year}년 ${monthOfYear + 1}월 ${dayOfMonth}일"

                // 현재 시간으로 TimePickerDialog 초기화
                val calendar = Calendar.getInstance()
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)

                // TimePickerDialog 생성
                val timePickerDialog = TimePickerDialog(context, { _, hourOfDay, minute ->
                    // 여기서 선택된 시간을 처리할 수 있습니다.
                    val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                    // 선택된 날짜와 시간을 사용하거나 처리합니다.
                    var selectedDateTime = "$selectedDate $selectedTime"
                    selectedDateTime = selectedDateTime.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm"))
                    dateTimeListener?.onDateTimeSelected(selectedDateTime)
                }, hour, minute, false)

                // TimePickerDialog 보이기
                timePickerDialog.show()
            }

        // 현재 날짜로 DatePickerDialog 초기화
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // DatePickerDialog 생성
        val datePickerDialog = DatePickerDialog(context, dateSetListener, year, month, day)

        // DatePickerDialog 보이기
        datePickerDialog.show()
    }

    fun updateDate(dateTime:String) : String {
        try {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 HH:mm", Locale.KOREAN)
            val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:00.000")

            val parsedDateTime = LocalDateTime.parse(dateTime, inputFormatter)
            val formattedDateTime = parsedDateTime.format(outputFormatter)

            return formattedDateTime
        } catch (e: Exception) {
            return ""
        }


    }
}
