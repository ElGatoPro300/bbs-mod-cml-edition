package elgatopro300.bbs_cml.particles.components;

public interface IComponentBase
{
	public default int getSortingIndex()
	{
		return 0;
	}
}