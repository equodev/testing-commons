/****************************************************************************
**
** Copyright (C) 2021 Equo
**
** This file is part of the Equo SDK.
**
** Commercial License Usage
** Licensees holding valid commercial Equo licenses may use this file in
** accordance with the commercial license agreement provided with the
** Software or, alternatively, in accordance with the terms contained in
** a written agreement between you and Equo. For licensing terms
** and conditions see https://www.equo.dev/terms.
**
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3 as published by the Free Software
** Foundation. Please review the following
** information to ensure the GNU General Public License requirements will
** be met: https://www.gnu.org/licenses/gpl-3.0.html.
**
****************************************************************************/

package com.equo.testing.common.test;

import static org.junit.Assert.assertFalse;

import org.junit.Rule;
import org.junit.Test;

import com.equo.testing.common.util.EquoRule;

public class EquoRuleTest {

  @Rule
  public EquoRule rule = new EquoRule(this).runInNonUiThread();

  @Test
  public void ruleRunInNonUIThreadTest() {
    assertFalse(Thread.currentThread().getName().equals("main"));
  }
}
