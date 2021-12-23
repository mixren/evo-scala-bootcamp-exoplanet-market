package io.github.mixren.evoscalabootcampexoplanetmarket.fakes

import io.github.mixren.evoscalabootcampexoplanetmarket.exoplanet.domain.{Dec, Distance, Exoplanet, ExoplanetOfficialName, Mass, Ra, Radius, Year}

object FakeExoplanets {
  val realExoplanetName1: ExoplanetOfficialName = ExoplanetOfficialName("2I/Borisov")
  val realExoplanetName2: ExoplanetOfficialName = ExoplanetOfficialName("1RXS 1609 b")
  val realExoplanetName3: ExoplanetOfficialName = ExoplanetOfficialName("1SWASP J1407 b")
  val realExoplanetName4: ExoplanetOfficialName = ExoplanetOfficialName("16 Cyg B b")

  val exo1: Exoplanet = Exoplanet(1, realExoplanetName1, Some(Mass(13)), None,               None,                  Some(Ra(200.347819)), Some(Dec(9.134452)),      Some(Year(2011)))
  val exo2: Exoplanet = Exoplanet(2, realExoplanetName2, None,           Some(Radius(0.77)), None,                  Some(Ra(136.674787)), Some(Dec(19.9390135139)), Some(Year(2020)))
  val exo3: Exoplanet = Exoplanet(3, realExoplanetName3, None,           None,               Some(Distance(60.2)),  Some(Ra(80.9473181)), Some(Dec(5.7486729294)),  Some(Year(2001)))
  val exo4: Exoplanet = Exoplanet(4, realExoplanetName4, Some(Mass(6)),  None,               Some(Distance(124.8)), Some(Ra(54.56542)),   Some(Dec(11.84532507)),   Some(Year(1994)))
  val exoSeq = Seq(exo1, exo2, exo3, exo4)
}
