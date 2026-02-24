package elgatopro300.bbs_cml.particles;

public enum ParticleMaterial
{
	OPAQUE("particles_opaque"), ALPHA("particles_alpha"), BLEND("particles_blend");

	public final String id;

	public static ParticleMaterial fromString(String material)
	{
		for (ParticleMaterial mat : values())
		{
			if (mat.id.equals(material))
			{
				return mat;
			}
		}

		return OPAQUE;
	}

	private ParticleMaterial(String id)
	{
		this.id = id;
	}
}