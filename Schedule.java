package Main;

class Schedule										// 시간표 클래스
{
	static int num = 0;								// 만들어진 시간표 개수
	int size = 0;									// 시간표에 포함된 강의 개수
	boolean possibility = false;					// 최종 완성 여부
	Lecture [] lectures = new Lecture[10];			// 시간표에 포함된 강의 목록
	
	public Schedule()	// 생성자
	{
		for(int i = 0; i < lectures.length; i++)	// 강의 목록 배열 선언
		{
			lectures[i] = new Lecture();
		}
	}
}