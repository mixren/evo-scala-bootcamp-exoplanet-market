package io.github.mixren.evoscalabootcampexoplanetmarket.utils

import com.roundeights.hasher.Implicits._

object HashGenerator {
  def run(value: String): String = {
    value.sha256.hex
  }
}
