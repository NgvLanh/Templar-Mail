let currentPage = 0;
const pageSize = 10;
let schedules = [];
let templates = [];

$(document).ready(function () {
  loadSchedules(0);
  fillUserData();
  loadTemplates();

  $('#type').change(updateTimeOptions);
  $('#type, #sendTime').change(generateCronExpression);
  $('#cronExp').on('input change', function () {
    $('#generatedCron').text($(this).val());
  });
});

function fillUserData() {
  try {
    $.ajax({
      url: '/api/v1/users',
      method: 'GET',
      dataType: 'json',
      success: function (user) {
        $('#user-name').text(user.name);
        $('#picture').attr('src', user.picture || '/images/default-avatar.png');
      },
      error: function (e) {
        let msg = 'Không thể tải thông tin người dùng!';
        try {
          const res = JSON.parse(e.responseText || '{}');
          msg =
            (res.errors && Object.values(res.errors)[0]) || res.message || msg;
        } catch (_) {
          msg = e.responseText || msg;
        }
        Swal.fire('Lỗi', msg, 'error');
      },
    });
  } catch (err) {
    Swal.fire('Lỗi', err.message, 'error');
  }
}

function loadTemplates() {
  try {
    $.ajax({
      url: '/api/v1/templates',
      method: 'GET',
      success: function (response) {
        const templates = response.data.content;
        const templateSelect = $('#template');
        templateSelect.empty();
        templates.forEach((template) => {
          templateSelect.append(
            $('<option>', {
              value: template.id,
              text: template.name,
            })
          );
        });
      },
      error: function (e) {
        let msg = 'Không thể tải danh sách template!';
        try {
          const res = JSON.parse(e.responseText || '{}');
          msg =
            (res.errors && Object.values(res.errors)[0]) || res.message || msg;
        } catch (_) {
          msg = e.responseText || msg;
        }
        Swal.fire('Lỗi', msg, 'error');
      },
    });
  } catch (err) {
    Swal.fire('Lỗi', err.message, 'error');
  }
}

function loadSchedules(page) {
  try {
    $.ajax({
      url: `/api/v1/schedules?page=${page}&size=${pageSize}`,
      method: 'GET',
      dataType: 'json',
      success: function (data) {
        schedules = data.data.content;
        currentPage = page;
        renderSchedules(data.data.content);
        renderPagination(data.data.totalPages);
      },
      error: function (e) {
        let msg = 'Không thể tải dữ liệu!';
        try {
          const res = JSON.parse(e.responseText || '{}');
          msg =
            (res.errors && Object.values(res.errors)[0]) || res.message || msg;
        } catch (_) {
          msg = e.responseText || msg;
        }
        Swal.fire('Lỗi', msg, 'error');
      },
    });
  } catch (err) {
    Swal.fire('Lỗi', err.message, 'error');
  }
}

function renderSchedules(schedules) {
  const tbody = $('#scheduleList');
  tbody.empty();

  if (schedules.length === 0) {
    tbody.append(`
      <tr>
        <td colspan="9" class="text-center">Không có lịch gửi email nào.</td>
      </tr>
    `);
    return;
  }

  schedules.forEach((schedule, index) => {
    templates.push(schedule.emailTemplate);
    const row = `
            <tr>
                <td>${index + 1}</td>
                <td>${schedule.name}</td>
                <td>${schedule.type}</td>
                <td>${formatSendTime(schedule.cronExpression)}</td>
                <td class="schedule-email-list">
                  <div class="email-list">
                      ${schedule.receiverEmail
                        .split(',')
                        .map(
                          (email) =>
                            `<span class="email-badge">${email.trim()}</span>`
                        )
                        .join('<br>')}
                  </div>
                </td>
                <td>
                <button class="btn btn-info"
                 title="Xem template"
                 data-bs-toggle="modal" 
                 data-bs-target="#templateModal"
                onclick="viewTemplate(${schedule.emailTemplate?.id || null})">
                ${schedule.emailTemplate?.name || 'Chưa tải được template'}
                </button>
                </td>
                <td><code>${schedule.cronExpression}</code></td>
                <td style="${
                  formatTypeAndActive(schedule.status, 'status') === 'active'
                    ? 'color: green;'
                    : 'color: red;'
                }">${schedule.status}</td>
                <td>
                        <button class="btn btn-primary" onclick="editSchedule(${
                          schedule.id
                        })">
                            Sửa
                        </button>
                        <button class="btn btn-danger" onclick="deleteSchedule(${
                          schedule.id
                        })">
                            Xóa
                        </button>
                </td>
            </tr>
        `;

    tbody.append(row);
  });
}

function formatSendTime(expr) {
  const map = {
    // Hàng ngày
    '0 0 8 * * ?': '8h sáng hàng ngày',
    '0 0 9 * * ?': '9h sáng hàng ngày',
    '0 0 10 * * ?': '10h sáng hàng ngày',
    '0 0 17 * * ?': '17h hàng ngày',

    // Hàng tuần
    '0 0 8 ? * 2': '8h sáng mỗi thứ hai',
    '0 0 8 ? * 3': '8h sáng mỗi thứ ba',
    '0 0 8 ? * 4': '8h sáng mỗi thứ tư',
    '0 0 8 ? * 5': '8h sáng mỗi thứ năm',
    '0 0 8 ? * 6': '8h sáng mỗi thứ sáu',
    '0 0 8 ? * 7': '8h sáng mỗi thứ bảy',
    '0 0 8 ? * 1': '8h sáng mỗi chủ nhật',
    '0 0 9 ? * 2': '9h sáng mỗi thứ hai',
    '0 0 9 ? * 3': '9h sáng mỗi thứ ba',
    '0 0 9 ? * 4': '9h sáng mỗi thứ tư',
    '0 0 9 ? * 5': '9h sáng mỗi thứ năm',
    '0 0 9 ? * 6': '9h sáng mỗi thứ sáu',
    '0 0 9 ? * 7': '9h sáng mỗi thứ bảy',
    '0 0 9 ? * 1': '9h sáng mỗi chủ nhật',
    '0 0 15 ? * 6': '15h mỗi thứ sáu',

    // Hàng tháng
    '0 0 8 1 * ?': '8h sáng ngày mùng 1 hàng tháng',
    '0 0 9 15 * ?': '9h sáng ngày 15 hàng tháng',
    '0 0 10 L * ?': '10h sáng ngày cuối tháng',
    '0 15 10 1 1,4,7,10 ?': '10h15 ngày mùng 1 của tháng 1,4,7,10 (hàng quý)',
    '0 30 10 12 9 ?': '10h30 ngày 12/9',

    // Cron đặc biệt
    '0 0 9 20 9 ?': '9h sáng ngày 20/9',
    '0 0 17 18 9 ?': '17h ngày 18/9',
  };

  return map[expr.trim()] || expr;
}

function renderPagination(totalPages) {
  const pagination = $('#pagination');
  pagination.empty();

  for (let i = 0; i < totalPages; i++) {
    const li = `
            <li class="page-item ${i === currentPage ? 'active' : ''}">
                <a class="page-link" href="#" onclick="loadSchedules(${i})">${
      i + 1
    }</a>
            </li>
        `;
    pagination.append(li);
  }
}

function updateTimeOptions() {
  const type = $('#type').val();
  const timeSelect = $('#sendTime');
  const timeOptions = $('#timeOptions');
  const cronInput = $('#cronInputDiv');
  const generatedCron = $('#generatedCron');

  const options = {
    daily: [{ value: '0 0 8 * * ?', text: '8h sáng mỗi ngày' }],
    weekly: [
      { value: '0 0 9 ? * 2', text: '9h sáng mỗi thứ hai' },
      { value: '0 0 9 ? * 3', text: '9h sáng mỗi thứ ba' },
      { value: '0 0 9 ? * 4', text: '9h sáng mỗi thứ tư' },
      { value: '0 0 9 ? * 5', text: '9h sáng mỗi thứ năm' },
      { value: '0 0 9 ? * 6', text: '9h sáng mỗi thứ sáu' },
      { value: '0 0 9 ? * 7', text: '9h sáng mỗi thứ bảy' },
      { value: '0 0 9 ? * 1', text: '9h sáng mỗi chủ nhật' },
    ],
    monthly: [
      { value: '0 0 8 1 * ?', text: '8h sáng ngày mùng 1 hàng tháng' },
      { value: '0 0 9 15 * ?', text: '9h sáng ngày 15 hàng tháng' },
      { value: '0 0 10 L * ?', text: '10h sáng ngày cuối tháng' },
    ],
  };

  if (type === 'cron') {
    timeOptions.hide();
    cronInput.show();
    generatedCron.text($('#cronExp').val() || '---');
  } else {
    cronInput.hide();
    timeOptions.show();
    timeSelect.empty();

    (options[type] || []).forEach((opt) => {
      timeSelect.append(new Option(opt.text, opt.value));
    });

    generateCronExpression();
  }
}

function generateCronExpression() {
  const type = $('#type').val();
  const time = $('#sendTime').val();
  let cronExp = '';

  if (type === 'cron') {
    cronExp = $('#cronExp').val() || '';
  } else {
    cronExp = time || '';
  }

  $('#generatedCron').text(cronExp || '---');
}

function openCreateModal() {
  const form = $('#scheduleForm')[0];
  form.reset();
  $('#schedule-id').val('');
  $('#type').trigger('change');
  $('#isActive').prop('checked', true);
  $('#modal-title').text('Tạo lịch gửi email mới');
  $('#modal-save-button').off('click').on('click', saveSchedule);
  $('#scheduleModal').modal('show');
}

function saveSchedule() {
  try {
    const form = $('#scheduleForm')[0];
    const formData = new FormData(form);
    const data = {
      method: '1',
      name: formData.get('name'),
      type: formData.get('type').toLocaleUpperCase(),
      sendTime: formData.get('sendTime'),
      receiverEmail: formData.get('emails').trim(),
      templateId: formData.get('template'),
      status: formData.get('isActive') ? 'ACTIVE' : 'INACTIVE',
      cronExpression:
        formData.get('type') === 'cron'
          ? formData.get('cronExp').trim()
          : $('#generatedCron').text(),
    };

    const emails = data.receiverEmail.split(',').map((e) => e.trim());
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    for (const email of emails) {
      if (!emailRegex.test(email)) {
        Swal.fire('Lỗi', `Email không hợp lệ: ${email}`, 'error');
        return;
      }
    }

    if (!data.receiverEmail || !data.name) {
      Swal.fire('Lỗi', 'Vui lòng nhập đầy đủ thông tin.', 'error');
      return;
    }

    if (data.type === 'cron' && !data.cronExpression) {
      Swal.fire('Lỗi', 'Vui lòng nhập Cron Expression.', 'error');
      return;
    }

    if (!isValidQuartzCron(data.cronExpression)) {
      Swal.fire('Lỗi', 'Cron Expression không hợp lệ.', 'error');
      return;
    }

    $.ajax({
      url: '/api/v1/schedules',
      method: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(data),
      success: async function () {
        await Swal.fire('Thành công', 'Tạo lịch gửi thành công!', 'success');
        form.reset();
        $('#scheduleModal').modal('hide');
        loadSchedules(currentPage);
      },
      error: function (e) {
        let msg = 'Không thể tạo lịch gửi!';
        try {
          const res = JSON.parse(e.responseText || '{}');
          msg =
            (res.errors && Object.values(res.errors)[0]) || res.message || msg;
        } catch (_) {
          msg = e.responseText || msg;
        }

        Swal.fire('Lỗi', msg, 'error');
      },
    });
  } catch (err) {
    Swal.fire('Lỗi', err.message?.errors?.receiverEmail, 'error');
  }
}

function editSchedule(id) {
  const schedule = schedules.find((s) => s.id === id);
  if (!schedule) {
    Swal.fire('Lỗi', 'Không tìm thấy lịch gửi!', 'error');
    return;
  }

  const form = $('#scheduleForm');
  form.find('[name="id"]').val(schedule.id);
  form.find('[name="name"]').val(schedule.name);
  form
    .find('[name="type"]')
    .val(formatTypeAndActive(schedule.type, 'type'))
    .change();
  if (formatTypeAndActive(schedule.type, 'type') === 'cron') {
    form.find('[name="cronExp"]').val(schedule.cronExpression).change();
  } else {
    form.find('[name="sendTime"]').val(schedule.cronExpression).change();
  }
  form.find('[name="emails"]').val(schedule.receiverEmail);
  form.find('[name="template"]').val(schedule.emailTemplate?.id).change();
  form
    .find('[name="isActive"]')
    .prop(
      'checked',
      formatTypeAndActive(schedule.status, 'status') === 'active'
    );

  if (schedule.type === 'cron') {
    form.find('[name="cronExp"]').val(schedule.cronExpression);
  }

  $('#modal-title').text('Chỉnh sửa lịch gửi email');
  $('#modal-save-button').off('click').on('click', updateSchedule);
  $('#scheduleModal').modal('show');
}

function updateSchedule() {
  try {
    const form = $('#scheduleForm')[0];
    const formData = new FormData(form);
    const data = {
      method: '0',
      id: formData.get('id'),
      name: formData.get('name'),
      type: formData.get('type').toLocaleUpperCase(),
      sendTime: formData.get('sendTime'),
      receiverEmail: formData.get('emails').trim(),
      templateId: formData.get('template'),
      status: formData.get('isActive') ? 'ACTIVE' : 'INACTIVE',
      cronExpression:
        formData.get('type') === 'cron'
          ? formData.get('cronExp').trim()
          : $('#generatedCron').text(),
    };

    const emails = data.receiverEmail.split(',').map((e) => e.trim());
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    for (const email of emails) {
      if (!emailRegex.test(email)) {
        Swal.fire('Lỗi', `Email không hợp lệ: ${email}`, 'error');
        return;
      }
    }

    if (!data.receiverEmail) {
      Swal.fire('Lỗi', 'Vui lòng nhập email người nhận.', 'error');
      return;
    }

    if (data.type === 'cron' && !data.cronExpression) {
      Swal.fire('Lỗi', 'Vui lòng nhập Cron Expression.', 'error');
      return;
    }

    $.ajax({
      url: '/api/v1/schedules',
      method: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(data),
      success: async function () {
        await Swal.fire(
          'Thành công',
          'Cập nhật lịch gửi thành công!',
          'success'
        );
        form.reset();
        $('#scheduleModal').modal('hide');
        loadSchedules(currentPage);
      },
      error: function (e) {
        let msg = 'Không thể cập nhật lịch gửi!';
        try {
          const res = JSON.parse(e.responseText || '{}');
          msg =
            (res.errors && Object.values(res.errors)[0]) || res.message || msg;
        } catch (_) {
          msg = e.responseText || msg;
        }
        Swal.fire('Lỗi', msg, 'error');
      },
    });
  } catch (err) {
    Swal.fire('Lỗi', err.message, 'error');
  }
}

function deleteSchedule(id) {
  const schedule = schedules.find((s) => s.id === id);
  // Gán chỉ để thoả kiểu dữ liệu do ko dùng REST API
  schedule.type = 'CRON';
  schedule.status = 'ACTIVE';
  if (!schedule) {
    Swal.fire('Lỗi', 'Không tìm thấy lịch gửi!', 'error');
    return;
  }

  Swal.fire({
    title: 'Xác nhận',
    text: 'Bạn có chắc chắn muốn xóa lịch gửi này?',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#d33',
    cancelButtonColor: '#3085d6',
    confirmButtonText: 'Xóa',
    cancelButtonText: 'Hủy',
  }).then((result) => {
    if (result.isConfirmed) {
      $.ajax({
        url: '/api/v1/schedules',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
          method: '-1',
          ...schedule,
        }),
        success: function () {
          loadSchedules(currentPage);
          Swal.fire('Đã xóa!', 'Lịch gửi đã được xóa.', 'success');
        },
        error: function (e) {
          let msg = 'Không thể xóa lịch gửi!';
          try {
            const res = JSON.parse(e.responseText || '{}');
            msg =
              (res.errors && Object.values(res.errors)[0]) ||
              res.message ||
              msg;
          } catch (_) {
            msg = e.responseText || msg;
          }
          Swal.fire('Lỗi', msg, 'error');
        },
      });
    }
  });
}

function viewTemplate(id) {
  const template = templates.find((t) => t.id === id);
  if (!template) {
    Swal.fire('Lỗi', 'Không tìm thấy template!', 'error');
    return;
  }

  $('#templateTitle').text(template.name || 'Xem Template');
  $('#templateSubject').text(template.subject || '(Không có tiêu đề)');
  $('#templateBody').html(template.body || '(Không có nội dung)');
  $('#templateModal').show();
}

function isValidQuartzCron(expr) {
  if (!expr || typeof expr !== 'string') return false;
  const parts = expr.trim().split(/\s+/);

  // Quartz: 6 hoặc 7 fields
  if (parts.length < 6 || parts.length > 7) return false;

  const validators = [
    { name: 'second', min: 0, max: 59 },
    { name: 'minute', min: 0, max: 59 },
    { name: 'hour', min: 0, max: 23 },
    {
      name: 'dayOfMonth',
      min: 1,
      max: 31,
      allowQuestion: true,
      allowSpecial: true,
    },
    {
      name: 'month',
      min: 1,
      max: 12,
      aliases: [
        'JAN',
        'FEB',
        'MAR',
        'APR',
        'MAY',
        'JUN',
        'JUL',
        'AUG',
        'SEP',
        'OCT',
        'NOV',
        'DEC',
      ],
    },
    {
      name: 'dayOfWeek',
      min: 1,
      max: 7,
      aliases: ['SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT'],
      allowQuestion: true,
      allowSpecial: true,
    },
    { name: 'year', min: 1970, max: 2099, optional: true },
  ];

  function checkField(field, spec) {
    if (field === '*') return true;
    if (spec.allowQuestion && field === '?') return true;

    // list
    if (field.includes(',')) {
      return field.split(',').every((f) => checkField(f, spec));
    }

    // step
    if (field.includes('/')) {
      const [base, step] = field.split('/');
      if (!checkField(base, spec)) return false;
      return /^\d+$/.test(step) && Number(step) > 0;
    }

    // range
    if (field.includes('-')) {
      const [a, b] = field.split('-');
      return checkField(a, spec) && checkField(b, spec);
    }

    // special Quartz extensions
    if (spec.allowSpecial) {
      if (/^\d+W$/.test(field)) return true; // nearest weekday
      if (/^\d+L$/.test(field)) return true; // last day of month
      if (/^\d+#\d+$/.test(field)) return true; // nth day of week
      if (field === 'L') return true; // last
    }

    // alias (month/day-of-week names)
    if (spec.aliases && spec.aliases.includes(field.toUpperCase())) return true;

    // number
    if (/^\d+$/.test(field)) {
      const n = Number(field);
      return n >= spec.min && n <= spec.max;
    }

    return false;
  }

  for (let i = 0; i < parts.length; i++) {
    const spec = validators[i];
    if (!spec) return false;
    if (spec.optional && i === parts.length - 1) {
      if (!checkField(parts[i], spec)) return false;
    } else {
      if (!checkField(parts[i], spec)) return false;
    }
  }
  return true;
}

function formatTypeAndActive(data, type) {
  if (type === 'type') {
    return data === 'Hàng ngày'
      ? 'daily'
      : data === 'Hàng tuần'
      ? 'weekly'
      : data === 'Hàng tháng'
      ? 'monthly'
      : 'cron';
  } else if (type === 'status') {
    return data === 'Hoạt động' ? 'active' : 'inactive';
  }
}
