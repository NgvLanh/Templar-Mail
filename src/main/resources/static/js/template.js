let currentPage = 0;
const pageSize = 10;
var templates = [];
let searchTerm = '';

function debounce(func, wait) {
  let timeout;
  return function (...args) {
    clearTimeout(timeout);
    timeout = setTimeout(() => func.apply(this, args), wait);
  };
}

$(document).ready(function () {
  $('#summernote').summernote({
    height: 300,
    callbacks: {
      onChange: function (contents) {
        $('#preview').html(contents);
      },
    },
  });

  $('#summernoteEdit').summernote({
    height: 300,
    callbacks: {
      onChange: function (contents) {
        $('#previewEdit').html(contents);
      },
    },
  });

  loadTemplates(0);
  fillUserData();

  $('#searchInput').on(
    'input',
    debounce(function (e) {
      searchTerm = e.target.value.trim();
      currentPage = 0;
      loadTemplates(0);
    }, 500)
  );

  $('#templateModal').on('hidden.bs.modal', function () {
    const form = $('#templateForm')[0];
    form.reset();
    $('#summernote').summernote('reset');
    $('#preview').html('');
    $('#modal-title').text('Thêm Template Email');
    $('#modal-save-button').show();

    $('#templateForm').find('[name="name"]').prop('disabled', false);
    $('#templateForm').find('[name="subject"]').prop('disabled', false);
    $('#summernote').summernote('enable');
    $('#templateForm').find('[name="id"]').val('');
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

function loadTemplates(page) {
  try {
    const searchParam = searchTerm
      ? `&search=${encodeURIComponent(searchTerm)}`
      : '';
    $.ajax({
      url: `/api/v1/templates?page=${page}&size=${pageSize}${searchParam}`,
      method: 'GET',
      dataType: 'json',
      success: function (data) {
        templates = data.data.content;
        currentPage = page;
        renderTemplates(data.data.content);
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

function renderTemplates(templates) {
  const tbody = $('#templateList');
  tbody.empty();
  if (templates.length === 0) {
    tbody.append(`
      <tr>
        <td colspan="9" class="text-center">Không có Email template nào.</td>
      </tr>
    `);
    return;
  }

  templates.forEach((template, index) => {
    const row = `
            <tr>
                <td>${index + 1}</td>
                <td>${template.name}</td>
                <td>${template.subject}</td>
                <td>${template.numUses}</td>
                <td>${new Date(template.createAt).toLocaleString()}</td>
                <td>
                    <button class="btn btn-info" onclick="viewTemplate(${
                      template.id
                    })">
                        Xem
                    </button>
                    <button class="btn btn-primary" onclick="editTemplate(${
                      template.id
                    })">
                        Sửa
                    </button>
                    <button class="btn btn-danger" onclick="deleteTemplate(${
                      template.id
                    })">
                        Xóa
                    </button>
                </td>
            </tr>
        `;
    tbody.append(row);
  });
}

function renderPagination(totalPages) {
  const pagination = $('#pagination');
  pagination.empty();

  for (let i = 0; i < totalPages; i++) {
    const li = `
            <li class="page-item ${i === currentPage ? 'active' : ''}">
                <a class="page-link" href="#" onclick="loadTemplates(${i})">${
      i + 1
    }</a>
            </li>
        `;
    pagination.append(li);
  }
}

function openCreateModal() {
  const form = $('#templateForm')[0];
  form.reset();
  $('#template-id').val('');
  $('#summernote').summernote('reset');
  $('#modal-title').text('Tạo Template Email Mới');
  $('#modal-save-button').off('click').on('click', saveTemplate);
  $('#templateModal').modal('show');
}

function saveTemplate() {
  try {
    const form = $('#templateForm')[0];
    const formData = new FormData(form);
    const name = formData.get('name').trim();
    const subject = formData.get('subject').trim();
    const body = formData.get('body').trim();

    if (!name || !subject || !body) {
      Swal.fire(
        'Lỗi',
        'Vui lòng điền đầy đủ tên, tiêu đề và nội dung.',
        'error'
      );
      return;
    }

    const data = { method: '1', name, subject, body };

    $.ajax({
      url: '/api/v1/templates',
      method: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(data),
      success: async function () {
        await Swal.fire('Thành công', 'Tạo template thành công!', 'success');
        form.reset();
        $('#summernote').summernote('reset');
        $('#preview').html('');
        $('#templateModal').modal('hide');
        loadTemplates(currentPage);
      },
      error: function (e) {
        let msg = 'Không thể tạo template!';
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

function editTemplate(id) {
  const template = templates.find((t) => t.id === id);
  if (!template) {
    Swal.fire('Lỗi', 'Không tìm thấy template!', 'error');
    return;
  }

  const form = $('#templateForm');
  form.find('[name="id"]').val(template.id);
  form.find('[name="name"]').val(template.name);
  form.find('[name="subject"]').val(template.subject);
  $('#summernote').summernote('code', template.body);
  $('#modal-title').text('Chỉnh Sửa Template Email');
  $('#modal-save-button').off('click').on('click', updateTemplate);
  $('#templateModal').modal('show');
}

function updateTemplate() {
  try {
    const form = $('#templateForm')[0];
    const formData = new FormData(form);
    const id = formData.get('id');
    const name = formData.get('name').trim();
    const subject = formData.get('subject').trim();
    const body = formData.get('body').trim();

    if (!name || !subject || !body) {
      Swal.fire(
        'Lỗi',
        'Vui lòng điền đầy đủ tên, tiêu đề và nội dung.',
        'error'
      );
      return;
    }

    const data = { method: '0', id, name, subject, body };

    $.ajax({
      url: '/api/v1/templates',
      method: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(data),
      success: async function () {
        await Swal.fire(
          'Thành công',
          'Cập nhật template thành công!',
          'success'
        );
        form.reset();
        $('#summernote').summernote('reset');
        $('#preview').html('');
        $('#templateModal').modal('hide');
        loadTemplates(currentPage);
      },
      error: function (e) {
        let msg = 'Không thể cập nhật template!';
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

function deleteTemplate(id) {
  const template = templates.find((t) => t.id === id);
  Swal.fire({
    title: 'Xác nhận',
    text: 'Bạn có chắc chắn muốn xóa template này?',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#d33',
    cancelButtonColor: '#3085d6',
    confirmButtonText: 'Xóa',
    cancelButtonText: 'Hủy',
  }).then((result) => {
    if (result.isConfirmed) {
      $.ajax({
        url: `/api/v1/templates`,
        method: 'POST',
        data: JSON.stringify({ method: '-1', ...template }),
        contentType: 'application/json',
        success: function () {
          loadTemplates(currentPage);
          Swal.fire('Đã xóa!', 'Template đã được xóa.', 'success');
        },
        error: function (e) {
          let msg = 'Không thể xóa template!';
          try {
            if (e.status === 400) {
              msg = 'Template này đang được sử dụng và không thể xóa!';
            } else {
              const res = JSON.parse(e.responseText || '{}');
              msg =
                (res.errors && Object.values(res.errors)[0]) ||
                res.message ||
                msg;
            }
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
  const form = $('#templateForm');
  form.find('[name="id"]').val(template.id);
  form.find('[name="name"]').val(template.name).prop('disabled', true);
  form.find('[name="subject"]').val(template.subject).prop('disabled', true);
  $('#summernote').summernote('code', template.body);
  $('#summernote').summernote('disable');
  $('#modal-title').text('Xem Template Email');
  $('#modal-save-button').hide();
  $('#templateModal').modal('show');
}
