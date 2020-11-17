package Main;

class Lecture				// 강의 클래스
{
	String name;			// 강의 이름
	int credit;				// 학점
	int number_of_people;	// 인원 수
	String professor;		// 교수
	String building;		// 건물
	int classroom_1;		// 강의실 1
	int start_1;			// 강의 시작 시간 1
	int end_1;				// 강의 종료 시간 1
	int classroom_2;		// 강의실 2
	int start_2;			// 강의 시작 시간 2
	int end_2;				// 강의 종료 시간 2
	
	public Lecture()		// 생성자 1
	{
		this.name = "";
		this.credit = 0;
		this.number_of_people = 0;
		this.professor = "";
		this.building = "";
		this.classroom_1 = 0;
		this.start_1 = 0;
		this.end_1 = 0;
		this.classroom_2 = 0;
		this.start_2 = 0;
		this.end_2 = 0;
	}
	
	public Lecture(String name, int credit, int number_of_people,
			String professor,	String building,
			int classroom_1, int start_1, int end_1,
			int classroom_2, int start_2, int end_2)	// 생성자 2
	{
		this.name = name;
		this.credit = credit;
		this.number_of_people = number_of_people;
		this.professor = professor;
		this.building = building;
		this.classroom_1 = classroom_1;
		this.start_1 = start_1;
		this.end_1 = end_1;
		this.classroom_2 = classroom_2;
		this.start_2 = start_2;
		this.end_2 = end_2;
	}
}
