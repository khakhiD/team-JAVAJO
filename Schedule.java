package Main;

class Schedule										// �ð�ǥ Ŭ����
{
	static int num = 0;								// ������� �ð�ǥ ����
	int size = 0;									// �ð�ǥ�� ���Ե� ���� ����
	boolean possibility = false;					// ���� �ϼ� ����
	Lecture [] lectures = new Lecture[10];			// �ð�ǥ�� ���Ե� ���� ���
	
	public Schedule()	// ������
	{
		for(int i = 0; i < lectures.length; i++)	// ���� ��� �迭 ����
		{
			lectures[i] = new Lecture();
		}
	}
}