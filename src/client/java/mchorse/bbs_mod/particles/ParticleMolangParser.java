package elgatopro300.bbs_cml.particles;

import elgatopro300.bbs_cml.math.molang.MolangParser;
import elgatopro300.bbs_cml.particles.functions.GetParticleVariable;
import elgatopro300.bbs_cml.particles.functions.SetParticleVariable;

public class ParticleMolangParser extends MolangParser
{
    public final ParticleScheme scheme;

    public ParticleMolangParser(ParticleScheme scheme)
    {
        this.scheme = scheme;

        this.functions.put("v.set", SetParticleVariable.class);
        this.functions.put("v.get", GetParticleVariable.class);
    }
}