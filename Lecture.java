package Main;

class Lecture				// ���� Ŭ����
{
	String name;			// ���� �̸�
	int credit;				// ����
	int number_of_people;	// �ο� ��
	String professor;		// ����
	String building;		// �ǹ�
	int classroom_1;		// ���ǽ� 1
	int start_1;			// ���� ���� �ð� 1
	int end_1;				// ���� ���� �ð� 1
	int classroom_2;		// ���ǽ� 2
	int start_2;			// ���� ���� �ð� 2
	int end_2;				// ���� ���� �ð� 2
	
	public Lecture()		// ������ 1
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
			int classroom_2, int start_2, int end_2)	// ������ 2
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
